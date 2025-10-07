package com.payments.microservices.msvc_payments.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payments.microservices.msvc_payments.entities.PaymentMethod;
import com.payments.microservices.msvc_payments.entities.PaymentStatus;
import com.payments.microservices.msvc_payments.entities.PaymentStatusResponse;
import com.payments.microservices.msvc_payments.request.PaymentCreateRequest;
import com.payments.microservices.msvc_payments.response.PaymentResponse;
import com.payments.microservices.msvc_payments.services.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;








@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments/qr")
@Slf4j
public class BankTransferPaymentController {

private final PaymentService paymentService;    

@PostMapping("/generate")
public ResponseEntity <PaymentResponse> createBankTransferPayment(@Valid @RequestBody PaymentCreateRequest request) {
 log.info("Creating bank transfer payment for appointment." + request.getAppointmentId(), request.getUserId());

 if (request.getPaymentMethod() != PaymentMethod.BANK_TRANSFER) {
    throw new IllegalArgumentException("This endpoint only supports BANK_TRANSFER payments.");
 }

 PaymentResponse response = paymentService.createPayment(request);
 
 log.info("Bank transfer payment created successfully ", response.getId(), response.getTransactionId());

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}


@PostMapping("/{paymentId}/process")
public ResponseEntity <PaymentResponse> processBankTransferPayment(@PathVariable Long paymentId) {
   log.info("Processing bank transfer payment.", paymentId);

   PaymentResponse response = paymentService.processPayment(paymentId);

   log.info("Bank transfer payment processed", response.getPaymentStatus(), response.getPaymentUrl());
    
    return ResponseEntity.ok(response);
}

@GetMapping("/{paymentId}")
public ResponseEntity <PaymentResponse>  getBankTransferPayment(@PathVariable Long paymentId) {
    log.info("Getting bank transfer payment", paymentId);

    PaymentResponse response = paymentService.getPaymentById(paymentId);

    if (response.getPaymentMethod() != PaymentMethod.BANK_TRANSFER) {
        throw new IllegalArgumentException("Payment is not a bank transfer");
    }

    return ResponseEntity.ok(response);
}

@GetMapping("/user/{userId}")
public ResponseEntity <List<PaymentResponse>>  getUserBankTransferPayments(@PathVariable Long userId) {
    log.info("Getting bank transfer payments by user", userId);

    List <PaymentResponse> allPayments = paymentService.getPaymentsByUserId(userId);

    List <PaymentResponse> transfers = allPayments.stream()
    .filter(p -> p.getPaymentMethod() == PaymentMethod.BANK_TRANSFER).toList();

    log.info("Found bank transfer payments for user", transfers.size(), userId);

    return ResponseEntity.ok(transfers);
}

@GetMapping("/shop/{shopId}")
public ResponseEntity <List<PaymentResponse>> getShopBankTransferPayments(@PathVariable Long shopId) {
 log.info("Getting bank transfer payments by shop", shopId);
 
 List <PaymentResponse> allPayments = paymentService.getPaymentsByShopId(shopId);

   List <PaymentResponse> transfers = allPayments.stream()
    .filter(p -> p.getPaymentMethod() == PaymentMethod.BANK_TRANSFER).toList();

    log.info("Found bank transfer payments for shop", transfers.size(), shopId);

    return ResponseEntity.ok(transfers);


}

@GetMapping("/status/{paymentId}")
public ResponseEntity <PaymentStatusResponse> checkBankTransferStatus(@PathVariable Long paymentId) {
    log.info("checking bank transfer payment status.", paymentId);

    PaymentResponse response = paymentService.getPaymentById(paymentId);

    PaymentStatusResponse statusResponse = PaymentStatusResponse.builder()
    .paymentId(String.valueOf(response.getId()))
    .status(response.getPaymentStatus())
    .amount(response.getAmount())
    .createdAt(response.getCreatedAt())
    .isPending(response.getPaymentStatus() == PaymentStatus.PENDING)
    .isCompleted(response.getPaymentStatus() == PaymentStatus.COMPLETED)
    .build();

    return ResponseEntity.ok(statusResponse);
}

@PostMapping("/cancel/{paymentId}")
public ResponseEntity <PaymentResponse> cancelBankTransferPayment(@PathVariable Long paymentId, @RequestParam Long userId) {
    log.info("Cancel bank transfer payment.", paymentId, userId);

    PaymentResponse response = paymentService.getPaymentById(paymentId);

    if (response.getPaymentMethod() != PaymentMethod.BANK_TRANSFER) {
        throw new IllegalArgumentException("Payment is not a bank transfer.");
    }

    if (response.getPaymentStatus() != PaymentStatus.PENDING) {
        throw new IllegalArgumentException("Only pending payments can be cancelled");
    }

    paymentService.deletePayment(paymentId); //aca tambien deberiamos poner el usuario dado que no cualquier usuario deberia de poder cancelar esto. VER EL SERVICIO

    log.info("Bank transfer payment cancelled successfully", paymentId);

    return ResponseEntity.noContent().build();
}

@GetMapping("/appointment/{appointmentId}/canp-pay")
public String getMethodName(@RequestParam String param) {
    return new String();
}







}