package com.finpay.payments.handler;

import java.math.BigDecimal;
import java.util.Map;

public class RetryPayload {

    private String transactionId;
    private BigDecimal amount;
    private String currency;
    private String processorId;
    private Map<String, String> optionalFields;

    public String getTransactionId() { return transactionId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getProcessorId() { return processorId; }
    public Map<String, String> getOptionalFields() { return optionalFields; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final RetryPayload payload = new RetryPayload();

        public Builder transactionId(String id) { payload.transactionId = id; return this; }
        public Builder amount(BigDecimal amount) { payload.amount = amount; return this; }
        public Builder currency(String currency) { payload.currency = currency; return this; }
        public Builder processorId(String processorId) { payload.processorId = processorId; return this; }
        public Builder optionalFields(Map<String, String> fields) { payload.optionalFields = fields; return this; }
        public RetryPayload build() { return payload; }
    }
}
