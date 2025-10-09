package com.payments.microservices.msvc_payments.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.payments.microservices.msvc_payments.services.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/webhooks")
public class MercadoPagoWebhookController {
private final PaymentService paymentService;

//esto nos va a servir para poder recibir las notif de pago, el pago se va a confirmar cuando MP detecte la transferencia (IMPORTANTE).

@PostMapping("/mercadopago")
public ResponseEntity<String> handleWebhook(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String id,
            @RequestParam(value = "data.id", required = false) String dataId,
            @RequestBody(required = false) String payload) {

                    try {
            // MercadoPago envía notificaciones cuando un pago cambia de estado
            if ("payment".equals(type)) {
                String paymentId = id != null ? id : dataId;
                
                if (paymentId != null) {
                    // Confirmar el pago automáticamente
                    paymentService.confirmPaymentForWebhook(paymentId, paymentId);
                    log.info("✅ Payment {} confirmed successfully via webhook", paymentId);
                } else {
                    log.warn(" Webhook received but no payment ID found");
                }
            } else {
                log.info(" Webhook type '{}' ignored (not a payment)", type);
            }
            
          
            return ResponseEntity.ok("OK");
            
        } catch (Exception e) {
            log.error(" Error processing MercadoPago webhook", e);
            return ResponseEntity.ok("ERROR");
        }
    }

}






