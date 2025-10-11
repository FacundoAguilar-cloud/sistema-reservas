package com.payments.microservices.msvc_payments.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payments.microservices.msvc_payments.entities.PaymentMethod;
import com.payments.microservices.msvc_payments.entities.PaymentStatus;
import com.payments.microservices.msvc_payments.entities.PaymentStatusResponse;
import com.payments.microservices.msvc_payments.request.PaymentCreateRequest;
import com.payments.microservices.msvc_payments.request.PaymentStatusUpdateRequest;
import com.payments.microservices.msvc_payments.response.CanPayResponse;
import com.payments.microservices.msvc_payments.response.PaymentResponse;
import com.payments.microservices.msvc_payments.services.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;








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


@PostMapping("/process/{paymentId}")
public ResponseEntity <PaymentResponse> processCreditCardPayment(@PathVariable Long paymentId) {
    log.info("Processing credit card payment.");

    PaymentResponse response = paymentService.processPayment(paymentId);

    log.info("Credit card payment processed.", response.getPaymentStatus());

    return ResponseEntity.ok(response);
}

@GetMapping("/{paymentId}")
public ResponseEntity <PaymentResponse> getCreditCardPayment(@PathVariable Long paymentId) {
    log.info("Getting credit card payment details.");
    
    PaymentResponse response = paymentService.getPaymentById(paymentId);

    if (response.getPaymentMethod() != PaymentMethod.CREDIT_CARD) {
        throw new IllegalArgumentException("Payment is not a credit card payment");
    }

    return ResponseEntity.ok(response);
}

@GetMapping("/user/{userId}")
public ResponseEntity<List<PaymentResponse>> getUserCrediCardPayments(@PathVariable Long userId) {
    log.info("Getting credit card payments from user", userId);

    List <PaymentResponse> allPayments = paymentService.getPaymentsByUserId(userId);

    List <PaymentResponse> creditCardPayments = allPayments.stream()
    .filter(p ->p.getPaymentMethod() == PaymentMethod.CREDIT_CARD).toList();

    log.info("Found credit card payments from user", creditCardPayments.size(), userId);

    return ResponseEntity.ok(creditCardPayments);
}

@GetMapping("/shop/{shopId}")
public ResponseEntity <List<PaymentResponse>> getShopCreditCardPayments(@PathVariable Long shopId) {
    log.info("Getting credit card payments from shop", shopId);

    List <PaymentResponse> allPayments = paymentService.getPaymentsByUserId(shopId);

    List <PaymentResponse> creditCardPayments = allPayments.stream().filter(p->p.getPaymentMethod() == PaymentMethod.CREDIT_CARD).toList();

    log.info("Found credit card payments from shop", creditCardPayments.size(), shopId);

    return ResponseEntity.ok(creditCardPayments);
}



@GetMapping("/status/{paymentId}")
public ResponseEntity <PaymentStatusResponse> checkCreditCardStatus(@PathVariable Long paymentId) {
    log.info("Checking credit card payment status", paymentId);

    PaymentResponse response = paymentService.getPaymentById(paymentId);

    PaymentStatusResponse statusResponse = PaymentStatusResponse.builder()
    .paymentId (String.valueOf(response.getId()))
    .status(response.getPaymentStatus())
    .amount(response.getAmount())
    .createdAt(response.getCreatedAt())
    .isPending(response.getPaymentStatus() == PaymentStatus.PENDING)
    .isCompleted(response.getPaymentStatus() == PaymentStatus.COMPLETED)
    .build();

    return ResponseEntity.ok(statusResponse);
}

@PostMapping("/refund/{paymentId}")
public ResponseEntity <PaymentResponse> refundCreditCardPayment(@PathVariable Long paymentId, @RequestParam Long userId) {
    log.info("Requesting refund for credit card payment.", paymentId, userId);

    PaymentResponse payment = paymentService.getPaymentById(paymentId);

    if (payment.getPaymentMethod() != PaymentMethod.CREDIT_CARD) {
        throw new IllegalArgumentException("Payment is not a credit card payment.");
    }

    if (payment.getPaymentStatus() != PaymentStatus.COMPLETED) {
        throw new IllegalArgumentException("Only completed payments can be cancelled.");
    }
    
    PaymentStatusUpdateRequest refundRequest = new PaymentStatusUpdateRequest();
    refundRequest.setPaymentStatus(PaymentStatus.REFUNDED); //esto vamos a tener que trabajarlo con la logica de MP

    PaymentResponse response = paymentService.updatePaymentStatus(refundRequest, paymentId, userId);

    log.info("Credit card payment refund requested",  paymentId);

     return ResponseEntity.ok(response);
}

@GetMapping("/appointment/{appointmentId}/can-pay")
public ResponseEntity <CanPayResponse> canPayAppointmentByCreditCard(@PathVariable Long appointmentId) {
    log.info("Checking if appointment can be paid with credit card");

    boolean canPay = paymentService.canAppointmentBePaid(appointmentId);

    CanPayResponse response = CanPayResponse.builder()
    .appointmentId(appointmentId)
    .canPay(canPay)
    .paymentMethod("CREDIT_CARD")
    .message(canPay ? 
    "Appointment can be pay by credit card." :
    "Payment already exist for this appointment.")
    .build();

    return ResponseEntity.ok(response);
}


private void validateCreditCardFields(PaymentCreateRequest request){
    if (request.getCardToken() == null || request.getCardToken().trim().isEmpty()) {
        throw new IllegalArgumentException("Card token is required for credit card payments.");
    }

    if (request.getCardHolderName() == null || request.getCardHolderName().trim().isEmpty()) {
        throw new IllegalArgumentException("Card holder name is required.");
    }

    if (request.getCardHolderEmail() == null || request.getCardHolderEmail().trim().isEmpty()) {
        throw new IllegalArgumentException("Cardholder email is required");
    }

    if (request.getCardHolderDocumentNumber() == null || request.getCardHolderDocumentNumber().trim().isEmpty()) {
        throw new IllegalArgumentException("Document number is required for credit card payments.");
    }
}



}
