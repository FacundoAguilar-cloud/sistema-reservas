package com.payments.microservices.msvc_payments.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payments.microservices.msvc_payments.security.validators.WebhookSignatureValidator;
import com.payments.microservices.msvc_payments.services.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/webhooks")
public class MercadoPagoWebhookController {

    private final PaymentService paymentService;
    private final WebhookSignatureValidator signatureValidator;

    private final ConcurrentHashMap<String, WebhookProcessingRecord> processedWebhooks = new ConcurrentHashMap<>();

    // Esto nos va a servir para poder recibir las notif de pago.
    // El pago se va a confirmar cuando MP detecte la transferencia (IMPORTANTE).

    @PostMapping("/mercadopago")
    public ResponseEntity<Map<String, Object>> handleWebhook(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long id,
            @RequestParam(value = "data.id", required = false) String dataId,
            @RequestHeader(value = "x-signature", required = false) String signature,
            @RequestHeader(value = "x-request-id", required = false) String requestId,
            @RequestBody(required = false) String payload) {

        try {
            log.info("Webhook received. Type: {}, ID: {}", type, id);

            if (signature == null || signature.trim().isEmpty()) {
                log.error("Missing signature header - Webhook REJECTED.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "WEBHOOK_REJECTED"));
            }

            if (!signatureValidator.isValidSignature(payload, signature)) {
                log.error("Webhook rejected, invalid signature.");
                log.debug("Payload: {}", payload);
                log.debug("Signature received: {}", signature);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "INVALID_SIGNATURE"));
            }

            log.info("Webhook signature validated successfully.");

            if (requestId != null && !requestId.trim().isEmpty()) {
                WebhookProcessingRecord existingRecord = processedWebhooks.get(requestId);
                if (existingRecord != null) {
                    long timeSinceProcessed = System.currentTimeMillis() - existingRecord.getTimestamp();
                    log.info("Webhook already processed. requestId: {}, timeSinceProcessed: {} ms", requestId, timeSinceProcessed);
                    return ResponseEntity.ok(Map.of("status", "success", "message", "Webhook already processed"));
                }
            }

            // Validación del tipo de notificación
            if (!"payment".equals(type)) {
                log.info("Webhook type ignored (not a payment notification). Type: {}", type);
                return ResponseEntity.ok(Map.of("message", "Notification type ignored"));
            }

            String paymentId = (id != null) ? id.toString() : dataId;
            if (paymentId == null || paymentId.trim().isEmpty()) {
                log.warn("Webhook received but no paymentId found in parameters.");
                return ResponseEntity.badRequest().body(Map.of("error", "Missing payment ID"));
            }

            log.info("Processing payment webhook. Payment ID: {}", paymentId);

            try {
                // Procesar el pago
                paymentService.confirmPaymentForWebhook(id, paymentId);
                
                // Marcar como procesado (registrar en cache)
                if (requestId != null && !requestId.trim().isEmpty()) {
                    processedWebhooks.put(requestId, new WebhookProcessingRecord(
                        requestId,
                        id,
                        System.currentTimeMillis()
                    ));
                    
                    // Limpiar registros antiguos (más de 1 hora)
                    cleanupOldWebhooks();
                }
                
                log.info("✅ Payment confirmed successfully via webhook - PaymentID: {}", id);
                
                return ResponseEntity
                    .ok(Map.of("status", "success", "message", "Payment processed"));
                
            } catch (Exception processingError) {
                log.error(" Error processing payment webhook - PaymentID: {}", id, processingError);
                
                // Importante: Retornar 200 para que MercadoPago no reintente indefinidamente
                // pero indicar que hubo un error en el procesamiento
                return ResponseEntity
                    .ok(Map.of("status", "error", "message", "Processing failed", 
                              "detail", processingError.getMessage()));
            }

        } catch (Exception e) {
            log.error(" Unexpected error processing MercadoPago webhook", e);
            
            // Retornar 200 para evitar reintentos infinitos de MercadoPago
            return ResponseEntity
                .ok(Map.of("status", "error", "message", "Internal error"));
        }
    }

    @lombok.Data
    @lombok.AllArgsConstructor 
    private static class WebhookProcessingRecord {
        private String requestId;
        private Long id;
        private long timestamp;
    }

    private void cleanupOldWebhooks() {
        long oneHourAgo = System.currentTimeMillis() - (60 * 60 * 1000);
        int removedCount = 0;
        
        for (Map.Entry<String, WebhookProcessingRecord> entry : processedWebhooks.entrySet()) {
            if (entry.getValue().getTimestamp() < oneHourAgo) {
                processedWebhooks.remove(entry.getKey());
                removedCount++;
            }
        }
        if (removedCount > 0) {
            log.debug("🧹 Cleaned up {} old webhook records", removedCount);
        }
    }

}








