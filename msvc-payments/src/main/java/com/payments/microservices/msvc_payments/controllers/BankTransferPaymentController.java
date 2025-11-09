package com.payments.microservices.msvc_payments.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payments.microservices.msvc_payments.entities.PaymentMethod;
import com.payments.microservices.msvc_payments.entities.PaymentStatus;
import com.payments.microservices.msvc_payments.entities.PaymentStatusResponse;
import com.payments.microservices.msvc_payments.request.PaymentCreateRequest;
import com.payments.microservices.msvc_payments.response.CanPayResponse;
import com.payments.microservices.msvc_payments.response.PaymentResponse;
import com.payments.microservices.msvc_payments.security.services.IdempotencyService;
import com.payments.microservices.msvc_payments.services.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments/transf")
@Slf4j
public class BankTransferPaymentController {

private final PaymentService paymentService;
private final IdempotencyService idempotencyService;

@PostMapping("/generate")
@PreAuthorize("hasRole('USER')")
public ResponseEntity <PaymentResponse> createBankTransferPayment(
    @Valid @RequestBody PaymentCreateRequest request,
    @RequestHeader("X-Idempotency-Key") String idempotencyKey,
    Authentication authentication,
    HttpServletRequest httpRequest ) {
    Long authenticatedUserId = Long.parseLong(authentication.getName());
        
        log.info("Creating bank transfer payment for appointment." + request.getAppointmentId(), request.getUserId());

        if (request.getUserId().equals(authenticatedUserId)) {
            log.warn("User ID mismatch", authenticatedUserId, request.getUserId());

            throw new SecurityException("User ID mismatch");
        }

        
 if (request.getPaymentMethod() != PaymentMethod.BANK_TRANSFER) {
    throw new IllegalArgumentException("This endpoint only supports BANK_TRANSFER payments.");
 }

 idempotencyService.validateIdempotencyKey(idempotencyKey);

 if (idempotencyService.isDuplicateRequest(idempotencyKey)) {
    PaymentResponse existingPayment = paymentService.getPaymentByIdempotencyKey(idempotencyKey);
    log.info("Returning existing payment for idempotency key.");
    return ResponseEntity.ok(existingPayment);
 }
 log.info("Bank transfer payment created successfully.");

 String clientIp = getClientIp(httpRequest);
String userAgent = httpRequest.getHeader("User-Agent");

 PaymentResponse response = paymentService.createPayment(request, idempotencyKey, clientIp, userAgent);
 
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

@GetMapping("/appointment/{appointmentId}/can-pay") //FALTA TERMINAR ESTE METODO EN EL SERVICIO VER
public ResponseEntity <CanPayResponse> canPayAppointmentByTransfer(@PathVariable Long appointmentId) {
    log.info("Checking if appointment can be paid by bank transfer.", appointmentId);

    boolean canPay = paymentService.canAppointmentBePaid(appointmentId);

    CanPayResponse response = CanPayResponse.builder()
    .appointmentId(appointmentId)
    .canPay(canPay)
    .paymentMethod("BANK_TRANSFER")
    .message(canPay ? 
    "Appointment can be pay by bank transfer." :
    "Payment already exist for this appointment.")
    .build();

    return ResponseEntity.ok(response);
}

//metodos de utilidades (extraccion de ip)

private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        // Si viene con múltiples IPs, tomar la primera
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }







}