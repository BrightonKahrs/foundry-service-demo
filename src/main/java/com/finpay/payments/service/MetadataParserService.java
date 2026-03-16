package com.finpay.payments.service;

import com.finpay.payments.model.Transaction;

import java.util.Map;

/**
 * Extracts and parses metadata from transactions.
 * Created as part of PAY-892 metadata versioning prep work.
 */
public class MetadataParserService {

    /**
     * Extracts optional metadata fields from a transaction.
     * @param transaction the transaction to extract fields from
     * @return map of optional field key-value pairs
     */
    public Map<String, String> extractOptionalFields(Transaction transaction) {
        // TODO: add input validation
        return transaction.getMetadata().getOptionalFields();
    }
}
