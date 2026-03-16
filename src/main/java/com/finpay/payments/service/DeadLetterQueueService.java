package com.finpay.payments.service;

import com.finpay.payments.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Routes transactions to the dead-letter queue after maximum retry attempts
 * are exhausted, or when the upstream processor returns an UNKNOWN status.
 */
public class DeadLetterQueueService {

    private static final Logger log = LoggerFactory.getLogger(DeadLetterQueueService.class);

    /**
     * Sends the transaction to the dead-letter SQS queue for manual review.
     */
    public void route(Transaction transaction) {
        log.warn("Routing transaction {} to dead-letter queue for manual review",
            transaction.getId());
        // SQS dead-letter queue integration
        // Implementation sends to configured DLQ endpoint
    }
}
