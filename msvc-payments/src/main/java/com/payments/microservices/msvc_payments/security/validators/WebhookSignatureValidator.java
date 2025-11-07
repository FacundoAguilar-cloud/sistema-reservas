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

    @Value("${webhook.secret}")
    private String webhookSecret;

    public boolean isValidSignature(String payload, String signature) {
        if (payload == null || signature == null) {
            log.warn("Webhook validation failed, payload or signature is null.");
            return false;
        }

        if (webhookSecret == null || webhookSecret.trim().isEmpty()) {
            log.error("Webhook secret not configured, all webhooks will be rejected.");
            return false;
        }

        try {
            String calculatedSignature = calculateHmacSHA256(payload, webhookSecret);
            boolean matches = MessageDigest.isEqual(calculatedSignature.getBytes(StandardCharsets.UTF_8),
                    signature.getBytes(StandardCharsets.UTF_8));
            if (!matches) {
                log.warn("Webhook validation failed, signature mismatch.");
            }
            return matches;
        } catch (Exception e) {
            log.error("Error validating webhook signature", e);
            return false;
        }
    }

    private String calculateHmacSHA256(String data, String key) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmac.init(secretKey);
        byte[] rawHmac = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(rawHmac);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}
