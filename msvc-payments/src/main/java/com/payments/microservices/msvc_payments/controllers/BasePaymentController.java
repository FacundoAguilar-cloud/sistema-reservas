package com.payments.microservices.msvc_payments.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import com.payments.microservices.msvc_payments.exceptions.DuplicateRequestException;
import com.payments.microservices.msvc_payments.response.PaymentResponse;
import com.payments.microservices.msvc_payments.security.services.IdempotencyService;
import com.payments.microservices.msvc_payments.services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BasePaymentController {

@Autowired
protected final PaymentService paymentService;
@Autowired
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

protected void processIdempotency(String idempotencyKey) {
    idempotencyService.validateIdempotencyKey(idempotencyKey);
    if (idempotencyService.isDuplicateRequest(idempotencyKey)) {
        PaymentResponse existingPayment = paymentService.getPaymentByIdempotencyKey(idempotencyKey);
        log.info("Returning existing payment for idempotency key: {}", idempotencyKey);
        throw new DuplicateRequestException(existingPayment);
    }
}

     protected String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }


    protected void validatePaymentAccess(PaymentResponse payment, Long authenticatedUserId, Authentication authentication) {
        if (!payment.getUserId().equals(authenticatedUserId) && 
            !authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new SecurityException("Not authorized to access this payment");
        }
    }



}
