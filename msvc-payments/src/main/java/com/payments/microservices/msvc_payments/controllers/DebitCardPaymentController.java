package com.payments.microservices.msvc_payments.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/payments/debit-card")
public class DebitCardPaymentController {

private final PaymentService paymentService;

public ResponseEntity <PaymentResponse> createDebitCardPayment(@Valid @RequestBody PaymentCreateRequest request){
    log.info("Creating debit card payment for appointment.", +  request.getAppointmentId(), request.getUserId());

    if (request.getPaymentMethod() != PaymentMethod.DEBIT_CARD) {
        throw new IllegalArgumentException("This endpoint only supports payment with debit card.");
    }

    validateDebitCardFields(request); //este metodo lo tenemos que crear, dejo esto a modo de recordatorio.

    PaymentResponse response = paymentService.createPayment(request);

    log.info("Debit card payment created successfully", response.getId(), response.getTransactionId());

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}

@PostMapping("/process/{paymentId}")
public ResponseEntity <PaymentResponse> processDebitCardPayment(@PathVariable Long paymentId) {
    log.info("Processing debit card payment.");

    PaymentResponse response = paymentService.processPayment(paymentId);

    log.info("Debit card payment processed.", response.getPaymentStatus());

    return ResponseEntity.ok(response);
}

@GetMapping("/{paymentId}")
public ResponseEntity <PaymentResponse> getDebitCardPayment(@PathVariable Long paymentId) {
    log.info("Getting debit card payment details.");
    
    PaymentResponse response = paymentService.getPaymentById(paymentId);

    if (response.getPaymentMethod() != PaymentMethod.DEBIT_CARD) {
        throw new IllegalArgumentException("Payment is not a debit card payment");
    }

    return ResponseEntity.ok(response);
}

@GetMapping("/user/{userId}")
public ResponseEntity<List<PaymentResponse>> getUserDebitCardPayments(@PathVariable Long userId) {
    log.info("Getting debit card payments from user", userId);

    List <PaymentResponse> allPayments = paymentService.getPaymentsByUserId(userId);

    List <PaymentResponse> debitCardPayments = allPayments.stream()
    .filter(p ->p.getPaymentMethod() == PaymentMethod.DEBIT_CARD).toList();

    log.info("Found debit card payments from user", debitCardPayments.size(), userId);

    return ResponseEntity.ok(debitCardPayments);
}

@GetMapping("/shop/{shopId}")
public ResponseEntity <List<PaymentResponse>> getShopDebitCardPayments(@PathVariable Long shopId) {
    log.info("Getting debit card payments from shop", shopId);

    List <PaymentResponse> allPayments = paymentService.getPaymentsByUserId(shopId);

    List <PaymentResponse> debitCardPayments = allPayments.stream().filter(p->p.getPaymentMethod() == PaymentMethod.DEBIT_CARD).toList();

    log.info("Found debit card payments from shop", debitCardPayments.size(), shopId);

    return ResponseEntity.ok(debitCardPayments);
}

@GetMapping("/status/{paymentId}")
public ResponseEntity <PaymentStatusResponse> checkDebitCardStatus(@PathVariable Long paymentId) {
    log.info("Checking debit card payment status", paymentId);

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
public ResponseEntity <PaymentResponse> refundDebitCardPayment(@PathVariable Long paymentId, @RequestParam Long userId) {
    log.info("Requesting refund for debit card payment.", paymentId, userId);

    PaymentResponse payment = paymentService.getPaymentById(paymentId);

    if (payment.getPaymentMethod() != PaymentMethod.DEBIT_CARD) {
        throw new IllegalArgumentException("Payment is not a credit card payment.");
    }

    if (payment.getPaymentStatus() != PaymentStatus.COMPLETED) {
        throw new IllegalArgumentException("Only completed payments can be cancelled.");
    }
    
    PaymentStatusUpdateRequest refundRequest = new PaymentStatusUpdateRequest();
    refundRequest.setPaymentStatus(PaymentStatus.REFUNDED); //esto vamos a tener que trabajarlo con la logica de MP

    PaymentResponse response = paymentService.updatePaymentStatus(refundRequest, paymentId, userId);

    log.info("Debit card payment refund requested",  paymentId);

     return ResponseEntity.ok(response);
}

@GetMapping("/appointment/{appointmentId}/can-pay")
public ResponseEntity <CanPayResponse> canPayAppointmentByDebitCard(@PathVariable Long appointmentId) {
    log.info("Checking if appointment can be paid with debit card");

    boolean canPay = paymentService.canAppointmentBePaid(appointmentId);

    CanPayResponse response = CanPayResponse.builder()
    .appointmentId(appointmentId)
    .canPay(canPay)
    .paymentMethod("DEBIT_CARD")
    .message(canPay ? 
    "Appointment can be pay by debit card." :
    "Payment already exist for this appointment.")
    .build();

    return ResponseEntity.ok(response);
}

private void validateDebitCardFields(PaymentCreateRequest request){
    if (request.getCardToken() == null || request.getCardToken().trim().isEmpty()) {
        throw new IllegalArgumentException("Card token is required for debit card payments.");
    }

    if (request.getCardHolderName() == null || request.getCardHolderName().trim().isEmpty()) {
        throw new IllegalArgumentException("Card holder name is required.");
    }

    if (request.getCardHolderEmail() == null || request.getCardHolderEmail().trim().isEmpty()) {
        throw new IllegalArgumentException("Cardholder email is required");
    }

    if (request.getCardHolderDocumentNumber() == null || request.getCardHolderDocumentNumber().trim().isEmpty()) {
        throw new IllegalArgumentException("Document number is required for debit card payments.");
    }
}



}
