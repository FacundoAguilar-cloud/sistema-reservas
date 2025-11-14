package com.payments.microservices.msvc_payments.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.payments.microservices.msvc_payments.entities.PaymentMethod;
import com.payments.microservices.msvc_payments.entities.PaymentStatus;
import com.payments.microservices.msvc_payments.entities.PaymentStatusResponse;
import com.payments.microservices.msvc_payments.request.PaymentCreateRequest;
import com.payments.microservices.msvc_payments.request.RefundRequest;
import com.payments.microservices.msvc_payments.response.CanPayResponse;
import com.payments.microservices.msvc_payments.response.PaymentResponse;
import com.payments.microservices.msvc_payments.security.services.IdempotencyService;
import com.payments.microservices.msvc_payments.services.PaymentService;
import com.payments.microservices.msvc_payments.services.RefundService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/payments/debit-card")
public class DebitCardPaymentController {

private final PaymentService paymentService;
private final RefundService refundService;
private final IdempotencyService idempotencyService;    

@PostMapping("/generate")
@PreAuthorize("hasRole('USER')")
public ResponseEntity <PaymentResponse> createDebitCardPayment(
    @Valid @RequestBody PaymentCreateRequest request, @RequestHeader("X-Idempotency-Key") String idempotencyKey,
    Authentication authentication,
    HttpServletRequest httpRequest){
    
    Long authenticatedUserId = Long.parseLong(authentication.getName());    
    
    log.info("Creating debit card payment for appointment.", +  request.getAppointmentId(), request.getUserId());

    
    if (request.getUserId().equals(authenticatedUserId)) {
            log.warn("User ID mismatch", authenticatedUserId, request.getUserId());

            throw new SecurityException("User ID mismatch");
        }
    
    if (request.getPaymentMethod() != PaymentMethod.DEBIT_CARD) {
        throw new IllegalArgumentException("This endpoint only supports payment with debit card.");
    }
    idempotencyService.findByIdIdempotencyKey(idempotencyKey);

    if (idempotencyService.isDuplicateRequest(idempotencyKey)) {
        PaymentResponse existingPayment = paymentService.getPaymentByIdempotencyKey(idempotencyKey);
        log.info("Returning existing payment for idempotency key.");
        return ResponseEntity.ok(existingPayment);
    }

     String clientIp = getClientIp(httpRequest);
     String userAgent = httpRequest.getHeader("User-Agent");

    validateDebitCardFields(request); 

    PaymentResponse response = paymentService.createPayment(request, idempotencyKey, clientIp, userAgent);

    log.info("Debit card payment created successfully", response.getId(), response.getTransactionId());

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}

@PostMapping("/process/{id}")
public ResponseEntity <PaymentResponse> processDebitCardPayment(@PathVariable Long id) {
    log.info("Processing debit card payment.");

    PaymentResponse response = paymentService.processPayment(id);

    log.info("Debit card payment processed.", response.getPaymentStatus());

    return ResponseEntity.ok(response);
}

@GetMapping("/{id}")
public ResponseEntity <PaymentResponse> getDebitCardPayment(@PathVariable Long id) {
    log.info("Getting debit card payment details.");
    
    PaymentResponse response = paymentService.getPaymentById(id);

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

@GetMapping("/status/{id}")
public ResponseEntity <PaymentStatusResponse> checkDebitCardStatus(@PathVariable Long id) {
    log.info("Checking debit card payment status", id);

    PaymentResponse response = paymentService.getPaymentById(id);

    PaymentStatusResponse statusResponse = PaymentStatusResponse.builder()
    .id (String.valueOf(response.getId()))
    .status(response.getPaymentStatus())
    .amount(response.getAmount())
    .createdAt(response.getCreatedAt())
    .isPending(response.getPaymentStatus() == PaymentStatus.PENDING)
    .isCompleted(response.getPaymentStatus() == PaymentStatus.COMPLETED)
    .build();

    return ResponseEntity.ok(statusResponse);
}

@PostMapping("/refund/{id}")
public ResponseEntity <PaymentResponse> refundDebitCardPayment(@PathVariable Long id, @RequestBody RefundRequest request) {
    log.info("Requesting refund for debit card payment.", id);

  PaymentResponse response = refundService.processFullRefund(id, request.getUserId(), request.getReason());

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
