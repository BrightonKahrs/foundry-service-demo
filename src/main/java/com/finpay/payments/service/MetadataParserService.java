package com.finpay.payments.service;

import com.finpay.payments.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

/**
 * Extracts and parses metadata from transactions.
 * Created as part of PAY-892 metadata versioning prep work.
 */
public class MetadataParserService {

    private static final Logger log = LoggerFactory.getLogger(MetadataParserService.class);

    /**
     * Extracts optional metadata fields from a transaction.
     * Returns empty map when transaction metadata is null, consistent
     * with previous behaviour in PaymentRetryHandler.
     *
     * @param transaction the transaction to extract fields from
     * @return map of optional field key-value pairs, or empty map if metadata is null
     */
    public Map<String, String> extractOptionalFields(Transaction transaction) {
        if (transaction.getMetadata() == null || transaction.getMetadata().getOptionalFields() == null) {
            log.warn("Transaction {} has null metadata in extractOptionalFields",
                transaction.getId());
            return Collections.emptyMap();
        }
        return transaction.getMetadata().getOptionalFields();
    }
}
