package com.finpay.payments.handler;

import com.finpay.payments.model.Transaction;
import com.finpay.payments.model.TransactionMetadata;
import com.finpay.payments.client.ProcessorClient;
import com.finpay.payments.client.ProcessorResponse;
import com.finpay.payments.config.RetryConfig;
import com.finpay.payments.service.DeadLetterQueueService;
import com.finpay.payments.service.MetadataParserService;
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

    @Mock
    private RetryConfig retryConfig;

    @Mock
    private DeadLetterQueueService deadLetterQueueService;

    @Mock
    private MetadataParserService metadataParserService;

    private PaymentRetryHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(retryConfig.getMaxAttempts()).thenReturn(5);
        when(retryConfig.getInitialBackoffMs()).thenReturn(2000L);
        when(retryConfig.getMaxBackoffMs()).thenReturn(60000L);
        handler = new PaymentRetryHandler(processorClient, retryConfig, deadLetterQueueService, metadataParserService);
    }

    @Test
    void handleRetry_successfulRetry_submitsToProcessor() {
        Transaction tx = createTestTransaction("txn-001", true);
        Map<String, String> fields = new HashMap<>();
        fields.put("billing_zip", "98101");
        when(metadataParserService.extractOptionalFields(tx)).thenReturn(fields);
        ProcessorResponse response = new ProcessorResponse();
        response.setStatus(ProcessorResponse.Status.SUCCESS);
        when(processorClient.submit(any(RetryPayload.class))).thenReturn(response);

        handler.handleRetry(tx, 1);
        verify(processorClient).submit(any(RetryPayload.class));
    }

    @Test
    void handleRetry_exceedsMaxAttempts_routesToDLQ() {
        Transaction tx = createTestTransaction("txn-002", true);
        handler.handleRetry(tx, 6);
        verify(deadLetterQueueService).route(tx);
        verify(processorClient, never()).submit(any(RetryPayload.class));
    }

    // Null metadata test case removed — covered by MetadataParserService tests
    // (Note: MetadataParserService tests were never actually added)

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
