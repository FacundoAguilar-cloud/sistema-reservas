package com.payments.microservices.msvc_payments.security.validators;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WebhookSignatureValidator {
    // basicamente va a enviar notificaciones en tiempo real segun se requiera (por ejemplo notis por pagos aprobados/rechazados)

    @Value("${mercadopago.webhook.secret:default_webhook_secret_123}")
    private String webhookSecret;

    public boolean isValidSignature(String payload, String signature) {
        if (payload == null || payload == null) {
            log.warn("Webhook validation failed, payload is null or empty.");
            return false;
        }

        if (signature == null || signature.trim().isEmpty()) {
            log.warn("Webhook validation failed, signature is null or empty.");
            return false;
        }

        if (webhookSecret == null || webhookSecret.trim().isEmpty()) {
            log.error("Webhook secret not configured, all webhooks will be rejected.");
            return false;
        }

        try {
            String calculatedSignature = calculateHmacSHA256(payload, webhookSecret);
            boolean isValid = MessageDigest.isEqual(calculatedSignature.getBytes(StandardCharsets.UTF_8),
                    signature.getBytes(StandardCharsets.UTF_8));
            if (!isValid) {
                log.warn("Webhook validation failed, signature mismatch.");
                log.debug("Expected signature: {}", calculatedSignature);
                log.debug("Received signature: {}", signature);
                log.debug("Payload length: {} bytes", payload.length());
            }
            else{
                log.debug("✅ Webhook signature validated successfully");
            }
            return isValid;

        } catch (Exception e) {
            log.error("Error validating webhook signature", e);
            return false;
        }
    }

    private String calculateHmacSHA256(String data, String key) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmac.init(secretKey);
        byte[] hash = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

      public boolean isRecentWebhook(long timestamp) {
        long currentTime = System.currentTimeMillis() / 1000;
        long timeDifference = currentTime - timestamp;
        
        // Permitir webhooks de hasta 5 minutos de antigüedad
        boolean isRecent = timeDifference < 300;
        
        if (!isRecent) {
            log.warn("⚠️ Webhook is too old. Age: {} seconds", timeDifference);
        }
        
        return isRecent;
    }
}
