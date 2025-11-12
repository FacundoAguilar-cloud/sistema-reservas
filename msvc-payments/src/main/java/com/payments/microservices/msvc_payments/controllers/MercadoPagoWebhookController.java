package com.payments.microservices.msvc_payments.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payments.microservices.msvc_payments.security.validators.WebhookSignatureValidator;
import com.payments.microservices.msvc_payments.services.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.cloud.client.loadbalancer.Response;
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

private final ConcurrentHashMap <String, WebhookProcessingRecord> processedWebhooks = new ConcurrentHashMap<>();

//esto nos va a servir para poder recibir las notif de pago, el pago se va a confirmar cuando MP detecte la transferencia (IMPORTANTE).

@PostMapping("/mercadopago")
public ResponseEntity<String> handleWebhook(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String id,
            @RequestParam(value = "data.id", required = false) String dataId,
            @RequestHeader(value = "x-signature", required = false) String signature,
            @RequestHeader(value = "x-request-id", required = false) String requestId,
            @RequestBody(required = false) String payload) {

        
            try {
                log.info("Webhook received.", type, id);

                if (signature == null || signature.trim().isEmpty()) {
                    log.error("Missing signature header - Webhook REJECTED.");

                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("WEBHOOK_REJECTED");
                }

                if (!signatureValidator.isValidSignature(payload, signature)) {
                    log.error("Webhook rejected, invalid signature.");
                    log.debug(payload);
                    log.debug("Signature received", signature);
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("INVALID_SIGNATURE");
                }
                log.info("Webhook signature validated successfully.");

                if (requestId != null && !requestId.trim().isEmpty()) {
                   WebhookProcessingRecord existingRecord = processedWebhooks.get(requestId);

                   long timeSinceProcessed = System.currentTimeMillis() - existingRecord.getTimestamp();
                   log.info("Webhook already processed", requestId, timeSinceProcessed);

                   return ResponseEntity.ok("success");
                    
                    //validacion del tipo de notificacion
                    if (!"payment".equals(type)) {
                        log.info("Webhook type ignored (not a payment notification)", type);

                        return ResponseEntity.ok("Notification type ignored");
                        }

                        String paymentId = id != null ? id : dataId;

                        if (paymentId == null || paymentId.trim().isEmpty()) {
                            log.warn("Webhook recieved but no paymentId found in parameters");

                            return ResponseEntity.badRequest().body("Missing payment ID");
                        }

                        log.info("Processing payment webhook", paymentId);
                        try {
                            paymentService.confirmPaymentForWebhook(paymentId, paymentId);
                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                        


             
    }


     private void cleanupOldWebhooks() {
        long oneHourAgo = System.currentTimeMillis() - (60 * 60 * 1000);
        processedWebhooks.entrySet()
            .removeIf(entry -> entry.getValue() < oneHourAgo);
    }


     @lombok.Data
     @lombok.AllArgsConstructor   
     private static class WebhookProcessingRecord {
        private String requestId;
        private String paymentId;
        private long timestamp;
    }

}








