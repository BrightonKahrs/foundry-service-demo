package com.finpay.payments.handler;

import com.finpay.payments.model.Transaction;
import com.finpay.payments.client.ProcessorClient;
import com.finpay.payments.client.ProcessorResponse;
import com.finpay.payments.config.RetryConfig;
import com.finpay.payments.service.DeadLetterQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

/**
 * PaymentRetryHandler is responsible for re-attempting failed transactions
 * with upstream payment processors (Stripe, Adyen, Braintree).
 * Invoked asynchronously via SQS queue after initial payment attempt
 * returns a retryable error code.
 */
public class PaymentRetryHandler {

    private static final Logger log = LoggerFactory.getLogger(PaymentRetryHandler.class);

    private final ProcessorClient processorClient;
    private final RetryConfig retryConfig;
    private final DeadLetterQueueService deadLetterQueueService;

    public PaymentRetryHandler(ProcessorClient processorClient,
                                RetryConfig retryConfig,
                                DeadLetterQueueService deadLetterQueueService) {
        this.processorClient = processorClient;
        this.retryConfig = retryConfig;
        this.deadLetterQueueService = deadLetterQueueService;
    }

    /**
     * Handles retry for a failed transaction.
     * Enforces max retry attempts with exponential backoff (capped).
     * Routes to dead-letter queue after max attempts exceeded.
     */
    public void handleRetry(Transaction transaction, int attemptNumber) {
        if (attemptNumber > retryConfig.getMaxAttempts()) {
            log.warn("Transaction {} exceeded max retry attempts ({}), routing to DLQ",
                transaction.getId(), retryConfig.getMaxAttempts());
            deadLetterQueueService.route(transaction);
            return;
        }

        try {
            // Exponential backoff with cap (fixes INC0031567 — retry storm)
            long backoffMs = Math.min(
                retryConfig.getInitialBackoffMs() * (long) Math.pow(2, attemptNumber - 1),
                retryConfig.getMaxBackoffMs()
            );
            Thread.sleep(backoffMs);

            // Parse optional metadata fields for enrichment
            Map<String, String> optionalFields = transaction.getMetadata().getOptionalFields();

            RetryPayload payload = buildRetryPayload(transaction, optionalFields);
            ProcessorResponse response = processorClient.submit(payload);

            // Route UNKNOWN status responses to DLQ instead of retrying forever
            if (response.getStatus() == ProcessorResponse.Status.UNKNOWN) {
                log.warn("Processor returned UNKNOWN for transaction {}, routing to DLQ",
                    transaction.getId());
                deadLetterQueueService.route(transaction);
                return;
            }

            log.info("Retry attempt {} submitted for transaction {}",
                attemptNumber, transaction.getId());

        } catch (Exception e) {
            log.error("Retry failed for transaction {}: {}",
                transaction.getId(), e.getMessage(), e);
            handleRetry(transaction, attemptNumber + 1);
        }
    }

    private RetryPayload buildRetryPayload(Transaction transaction, Map<String, String> optionalFields) {
        return RetryPayload.builder()
            .transactionId(transaction.getId())
            .amount(transaction.getAmount())
            .currency(transaction.getCurrency())
            .processorId(transaction.getProcessorId())
            .optionalFields(optionalFields)
            .build();
    }
}
