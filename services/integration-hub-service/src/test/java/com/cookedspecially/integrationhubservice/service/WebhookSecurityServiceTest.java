package com.cookedspecially.integrationhubservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class WebhookSecurityServiceTest {

    private WebhookSecurityService service;

    @BeforeEach
    void setUp() {
        service = new WebhookSecurityService();
        ReflectionTestUtils.setField(service, "signatureValidationEnabled", true);
    }

    @Test
    void testCalculateSignature() throws Exception {
        String payload = "{\"order_id\":\"12345\"}";
        String secret = "test-secret";

        String signature = service.calculateSignature(payload, secret);

        assertNotNull(signature);
        assertFalse(signature.isEmpty());
    }

    @Test
    void testValidateWebhookSignature_ValidSignature() throws Exception {
        String payload = "{\"order_id\":\"12345\"}";
        String secret = "test-secret";

        String signature = service.calculateSignature(payload, secret);
        boolean isValid = service.validateWebhookSignature(payload, signature, secret);

        assertTrue(isValid);
    }

    @Test
    void testValidateWebhookSignature_InvalidSignature() {
        String payload = "{\"order_id\":\"12345\"}";
        String secret = "test-secret";
        String invalidSignature = "invalid-signature";

        boolean isValid = service.validateWebhookSignature(payload, invalidSignature, secret);

        assertFalse(isValid);
    }

    @Test
    void testValidateWebhookSignature_NullSignature() {
        String payload = "{\"order_id\":\"12345\"}";
        String secret = "test-secret";

        boolean isValid = service.validateWebhookSignature(payload, null, secret);

        assertFalse(isValid);
    }

    @Test
    void testValidateWebhookSignature_DisabledValidation() {
        ReflectionTestUtils.setField(service, "signatureValidationEnabled", false);

        String payload = "{\"order_id\":\"12345\"}";
        String secret = "test-secret";
        String invalidSignature = "invalid";

        boolean isValid = service.validateWebhookSignature(payload, invalidSignature, secret);

        assertTrue(isValid); // Should pass when validation is disabled
    }
}
