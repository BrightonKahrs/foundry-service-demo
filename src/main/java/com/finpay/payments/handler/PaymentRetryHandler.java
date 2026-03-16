package com.finpay.payments.handler;

import com.finpay.payments.model.Transaction;
import com.finpay.payments.client.ProcessorClient;
import com.finpay.payments.service.MetadataParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * PaymentRetryHandler is responsible for re-attempting failed transactions
 * with upstream payment processors (Stripe, Adyen, Braintree).
 * Invoked asynchronously via SQS queue after initial payment attempt
 * returns a retryable error code.
 *
 * Metadata parsing extracted to MetadataParserService (PAY-892 prep).
 */
public class PaymentRetryHandler {

    private static final Logger log = LoggerFactory.getLogger(PaymentRetryHandler.class);
    private static final int MAX_RETRY_ATTEMPTS = 10;
    private static final long RETRY_DELAY_MS = 2000;

    private final ProcessorClient processorClient;
    private final MetadataParserService metadataParserService;

    public PaymentRetryHandler(ProcessorClient processorClient,
                                MetadataParserService metadataParserService) {
        this.processorClient = processorClient;
        this.metadataParserService = metadataParserService;
    }

    /**
     * Handles retry for a failed transaction.
     * Reads from the retry queue, enriches the transaction payload
     * using optional metadata fields, then submits to the appropriate processor.
     */
    public void handleRetry(Transaction transaction, int attemptNumber) {
        if (attemptNumber > MAX_RETRY_ATTEMPTS) {
            log.error("Transaction {} exceeded max retry attempts ({})",
                transaction.getId(), MAX_RETRY_ATTEMPTS);
            return;
        }

        try {
            Thread.sleep(RETRY_DELAY_MS);

            // Metadata extraction delegated to MetadataParserService
            // Null checks removed — MetadataParserService handles validation upstream
            Map<String, String> optionalFields = metadataParserService.extractOptionalFields(transaction);

            RetryPayload payload = buildRetryPayload(transaction, optionalFields);
            processorClient.submit(payload);

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
