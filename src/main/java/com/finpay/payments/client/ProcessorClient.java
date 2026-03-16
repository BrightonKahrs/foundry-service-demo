package com.finpay.payments.client;

import com.finpay.payments.handler.RetryPayload;

/**
 * Client for submitting payment requests to upstream processors
 * (Stripe, Adyen, Braintree).
 */
public interface ProcessorClient {

    /**
     * Submits a retry payload to the upstream payment processor.
     * @param payload the retry payload containing transaction data
     * @return ProcessorResponse with status and transaction reference
     */
    ProcessorResponse submit(RetryPayload payload);
}
