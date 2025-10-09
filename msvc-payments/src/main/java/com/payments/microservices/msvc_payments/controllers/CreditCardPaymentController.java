package com.payments.microservices.msvc_payments.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payments.microservices.msvc_payments.entities.PaymentMethod;
import com.payments.microservices.msvc_payments.response.PaymentResponse;
import com.payments.microservices.msvc_payments.services.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/payments/credit-card")
public class CreditCardPaymentController {

private final PaymentService paymentService;

public ResponseEntity <PaymentResponse> createCreditCardPayment(@Valid @RequestBody com.payments.microservices.msvc_payments.request.PaymentCreateRequest request){
    log.info("Creating credit card payment for appointment", +  request.getAppointmentId(), request.getUserId());

    if (request.getPaymentMethod() != PaymentMethod.CREDIT_CARD) {
        throw new IllegalArgumentException("This endpoint only supports payment with credit card.");
    }

    validateCreditCardFields(request); //este metodo lo tenemos que crear, dejo esto a modo de recordatorio.

    PaymentResponse response = paymentService.createPayment(request);

    log.info("Credit card payment created successfully", response.getId(), response.getTransactionId());

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}



}
