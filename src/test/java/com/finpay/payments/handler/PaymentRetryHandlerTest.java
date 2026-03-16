package com.finpay.payments.handler;

import com.finpay.payments.model.Transaction;
import com.finpay.payments.model.TransactionMetadata;
import com.finpay.payments.client.ProcessorClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentRetryHandlerTest {

    @Mock
    private ProcessorClient processorClient;

    private PaymentRetryHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new PaymentRetryHandler(processorClient);
    }

    @Test
    void handleRetry_successfulRetry_submitsToProcessor() {
        Transaction tx = createTestTransaction("txn-001", true);
        handler.handleRetry(tx, 1);
        verify(processorClient).submit(any(RetryPayload.class));
    }

    @Test
    void handleRetry_exceedsMaxAttempts_doesNotSubmit() {
        Transaction tx = createTestTransaction("txn-002", true);
        handler.handleRetry(tx, 11);
        verify(processorClient, never()).submit(any(RetryPayload.class));
    }

    @Test
    void handleRetry_nullMetadata_throwsNPE() {
        // BUG: This test documents the known issue — null metadata
        // causes NullPointerException. No null guard exists yet.
        Transaction tx = createTestTransaction("txn-003", false);
        tx.setMetadata(null);
        org.junit.jupiter.api.Assertions.assertThrows(
            NullPointerException.class,
            () -> handler.handleRetry(tx, 1)
        );
    }

    @Test
    void handleRetry_nullOptionalFields_throwsNPE() {
        Transaction tx = createTestTransaction("txn-004", false);
        TransactionMetadata metadata = new TransactionMetadata();
        metadata.setOptionalFields(null);
        tx.setMetadata(metadata);
        org.junit.jupiter.api.Assertions.assertThrows(
            NullPointerException.class,
            () -> handler.handleRetry(tx, 1)
        );
    }

    private Transaction createTestTransaction(String id, boolean withMetadata) {
        Transaction tx = new Transaction();
        tx.setId(id);
        tx.setIdempotencyKey("idem-" + id);
        tx.setAmount(new BigDecimal("49.99"));
        tx.setCurrency("USD");
        tx.setProcessorId("stripe");
        if (withMetadata) {
            TransactionMetadata metadata = new TransactionMetadata();
            Map<String, String> fields = new HashMap<>();
            fields.put("billing_zip", "98101");
            metadata.setOptionalFields(fields);
            tx.setMetadata(metadata);
        }
        return tx;
    }
}
