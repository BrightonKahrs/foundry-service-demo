package com.finpay.payments.config;

/**
 * Configuration for payment retry behaviour.
 * Controls backoff timing, max attempts, and DLQ routing.
 */
public class RetryConfig {

    private int maxAttempts = 5;
    private long initialBackoffMs = 2000;
    private long maxBackoffMs = 60000;

    public int getMaxAttempts() { return maxAttempts; }
    public void setMaxAttempts(int maxAttempts) { this.maxAttempts = maxAttempts; }

    public long getInitialBackoffMs() { return initialBackoffMs; }
    public void setInitialBackoffMs(long ms) { this.initialBackoffMs = ms; }

    public long getMaxBackoffMs() { return maxBackoffMs; }
    public void setMaxBackoffMs(long ms) { this.maxBackoffMs = ms; }
}
