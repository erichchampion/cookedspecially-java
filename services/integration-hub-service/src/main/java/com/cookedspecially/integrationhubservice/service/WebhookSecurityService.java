package com.cookedspecially.integrationhubservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
@Slf4j
public class WebhookSecurityService {

    @Value("${integration.webhook.signature-validation-enabled:true}")
    private boolean signatureValidationEnabled;

    public boolean validateWebhookSignature(String payload, String receivedSignature, String secret) {
        if (!signatureValidationEnabled) {
            log.warn("Webhook signature validation is disabled");
            return true;
        }

        if (receivedSignature == null || receivedSignature.isEmpty()) {
            log.warn("No signature provided for webhook validation");
            return false;
        }

        try {
            String calculatedSignature = calculateSignature(payload, secret);
            boolean isValid = calculatedSignature.equals(receivedSignature);

            if (!isValid) {
                log.warn("Webhook signature validation failed");
            }

            return isValid;
        } catch (Exception e) {
            log.error("Error validating webhook signature", e);
            return false;
        }
    }

    public String calculateSignature(String payload, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256Hmac.init(secretKey);

        byte[] hash = sha256Hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

    public boolean validateZomatoSignature(String payload, String receivedSignature, String secret) {
        return validateWebhookSignature(payload, receivedSignature, secret);
    }
}
