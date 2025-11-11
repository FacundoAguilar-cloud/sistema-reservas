package com.payments.microservices.msvc_payments.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payments.microservices.msvc_payments.security.validators.WebhookSignatureValidator;
import com.payments.microservices.msvc_payments.services.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

private final ConcurrentHashMap <String, Long> processedWebhooks = new ConcurrentHashMap<>();

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

                if (signature == null || !signatureValidator.isValidSignature(payload, signature)) {
                    log.error("Invalid webhook signature - REJECTED.");

                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("INVALID_SIGNATURE");
                }

                if (requestId !=null && processedWebhooks.containsKey(requestId)) {
                    log.info("Webhook already processed", requestId);
                    return ResponseEntity.ok("ALREADY_PROCESSED");
                }

                if ("payments".equals(type)) {
                    String paymentId = id != null ? id : dataId;

                    if (paymentId != null) {
                        log.info("Processing payment webhook.");

                        paymentService.confirmPaymentForWebhook(paymentId, paymentId); //REVISAR ESTO

                        if (requestId != null) {
                            processedWebhooks.put(paymentId, System.currentTimeMillis());
                            cleanupOldWebhooks();
                        }
                        log.info("Payment confirmed successfully via webhook", paymentId);
                        
                    } else{
                        log.warn("Webhook received but not payment ID found.");
                    }
                    
                } else{
                    log.warn("Webhook type ignored (not a payment.)", type);
                }
                return ResponseEntity.ok("OK");
            } catch (Exception e) {
                log.error("Error processing MercadoPago webhook.", e);

                return ResponseEntity.ok("ERROR_PROCESSED");
            }


             
    }


     private void cleanupOldWebhooks() {
        long oneHourAgo = System.currentTimeMillis() - (60 * 60 * 1000);
        processedWebhooks.entrySet()
            .removeIf(entry -> entry.getValue() < oneHourAgo);
    }

}






