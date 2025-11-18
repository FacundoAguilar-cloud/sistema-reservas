package com.payments.microservices.msvc_payments.controllers;

import com.payments.microservices.msvc_payments.security.services.IdempotencyService;
import com.payments.microservices.msvc_payments.services.PaymentService;

public abstract class BasePaymentController {
protected final PaymentService paymentService;
protected final IdempotencyService idempotencyService;

protected BasePaymentController(PaymentService paymentService, IdempotencyService idempotencyService){
    this.paymentService = paymentService;
    this.idempotencyService = idempotencyService;
}

protected void validateUserAuthorization(Long requestedUserId, Long authenticatedUserId) {
        if (!requestedUserId.equals(authenticatedUserId)) {
            log.warn("User ID mismatch. Authenticated: {}, Request: {}", 
                     authenticatedUserId, requestedUserId);
            throw new SecurityException("User ID mismatch.");
        
}
}



}
