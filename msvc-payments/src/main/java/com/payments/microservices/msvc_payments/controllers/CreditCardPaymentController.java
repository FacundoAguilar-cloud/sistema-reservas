package com.payments.microservices.msvc_payments.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;








@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/payments/credit-card")
public class CreditCardPaymentController {

private final PaymentService paymentService;
private final RefundService refundService;
private final IdempotencyService idempotencyService;

@PostMapping("/generate")
@PreAuthorize("hasRole('USER')")
public ResponseEntity <PaymentResponse> createCreditCardPayment(
    @Valid @RequestBody com.payments.microservices.msvc_payments.request.PaymentCreateRequest request, @RequestHeader("X-Idempotency-Key") String idempotencyKey,
    Authentication authentication,
    HttpServletRequest httpRequest){
    Long authenticatedUserId = Long.parseLong(authentication.getName());
    
    log.info("Creating credit card payment for appointment", +  request.getAppointmentId(), request.getUserId());

    if (!request.getUserId().equals(authenticatedUserId)) {
            log.warn("User ID mismatch", authenticatedUserId, request.getUserId());

            throw new SecurityException("User ID mismatch");
        }

    idempotencyService.validateIdempotencyKey(idempotencyKey);

    if (idempotencyService.isDuplicateRequest(idempotencyKey)) {
        PaymentResponse existingPayment = paymentService.getPaymentByIdempotencyKey(idempotencyKey);
        log.info("Returning existing payment for idempotency key.");
        return ResponseEntity.ok(existingPayment);
    }

     String clientIp = getClientIp(httpRequest);
     String userAgent = httpRequest.getHeader("User-Agent");

    validateCreditCardFields(request); 

    PaymentResponse response = paymentService.createPayment(request, idempotencyKey, clientIp, userAgent);

    log.info("Credit card payment created successfully", response.getId(), response.getTransactionId());

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}


@PostMapping("/process/{id}")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public ResponseEntity <PaymentResponse> processCreditCardPayment(@PathVariable Long id, Authentication authentication) {
    log.info("Processing credit card payment.");

    PaymentResponse existingPayment = paymentService.getPaymentById(id);
    Long authenticatedUserId = Long.parseLong(authentication.getName());

    if (!existingPayment.getUserId().equals(authenticatedUserId) && 
            !authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new SecurityException("Not authorized to process this payment");
        }
    PaymentResponse response = paymentService.processPayment(id);
    log.info("Credit card payment processed.", response.getPaymentStatus());
    return ResponseEntity.ok(response);
}

@GetMapping("/{id}")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public ResponseEntity <PaymentResponse> getCreditCardPayment(@PathVariable Long id, Authentication authentication) {
    log.info("Getting credit card payment details.");
    
    PaymentResponse response = paymentService.getPaymentById(id);

   Long authenticatedUserId = Long.parseLong(authentication.getName());
        if (!response.getUserId().equals(authenticatedUserId) && 
            !authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new SecurityException("Not authorized to view this payment");
        }

    return ResponseEntity.ok(response);
}

@GetMapping("/user/{userId}")
 @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public ResponseEntity<List<PaymentResponse>> getUserCreditCardPayments(@PathVariable Long userId, Authentication authentication) {
    log.info("Getting credit card payments from user", userId);

     Long authenticatedUserId = Long.parseLong(authentication.getName());
        if (!userId.equals(authenticatedUserId) && 
            !authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new SecurityException("Not authorized to view these payments");
        }

    List <PaymentResponse> allPayments = paymentService.getPaymentsByUserId(userId);

    List <PaymentResponse> creditCardPayments = allPayments.stream()
    .filter(p ->p.getPaymentMethod() == PaymentMethod.CREDIT_CARD).toList();

    log.info("Found credit card payments from user", creditCardPayments.size(), userId);

    return ResponseEntity.ok(creditCardPayments);
}

@GetMapping("/shop/{shopId}")
 @PreAuthorize("hasRole('SHOP_OWNER') or hasRole('ADMIN')")
public ResponseEntity <List<PaymentResponse>> getShopCreditCardPayments(@PathVariable Long shopId, Authentication authentication) {
    log.info("Getting credit card payments from shop", shopId);

    List <PaymentResponse> allPayments = paymentService.getPaymentsByUserId(shopId);

    List <PaymentResponse> creditCardPayments = allPayments.stream().filter(p->p.getPaymentMethod() == PaymentMethod.CREDIT_CARD).toList();

    log.info("Found credit card payments from shop", creditCardPayments.size(), shopId);

    return ResponseEntity.ok(creditCardPayments);
}



@GetMapping("/status/{id}")
public ResponseEntity <PaymentStatusResponse> checkCreditCardStatus(@PathVariable Long id) {
    log.info("Checking credit card payment status", id);

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
 @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public ResponseEntity <PaymentResponse> refundCreditCardPayment(@PathVariable Long id, @RequestBody RefundRequest request, Authentication authentication) {
   log.info("Requesting refund for debit card payment.", id);
   
    PaymentResponse existingPayment = paymentService.getPaymentById(id);
    Long authenticatedUserId = Long.parseLong(authentication.getName());

     if (!existingPayment.getUserId().equals(authenticatedUserId) && 
            !authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new SecurityException("Not authorized to refund this payment");
        }

  PaymentResponse response = refundService.processFullRefund(id, request.getUserId(), request.getReason());

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
