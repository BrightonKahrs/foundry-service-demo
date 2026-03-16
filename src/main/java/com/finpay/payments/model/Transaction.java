package com.finpay.payments.model;

import java.math.BigDecimal;

public class Transaction {

    private String id;
    private String idempotencyKey;
    private BigDecimal amount;
    private String currency;
    private String processorId;
    private TransactionMetadata metadata;
    private String status;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getProcessorId() { return processorId; }
    public void setProcessorId(String processorId) { this.processorId = processorId; }

    public TransactionMetadata getMetadata() { return metadata; }
    public void setMetadata(TransactionMetadata metadata) { this.metadata = metadata; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
