package com.finpay.payments.client;

public class ProcessorResponse {

    public enum Status {
        SUCCESS,
        DECLINED,
        RETRYABLE_ERROR,
        UNKNOWN
    }

    private Status status;
    private String transactionReference;
    private String errorMessage;

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String ref) { this.transactionReference = ref; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
