package com.payments.microservices.msvc_payments.security.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payments.microservices.msvc_payments.entities.Payment;
import com.payments.microservices.msvc_payments.repositories.PaymentRepository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// esta clase va a servir para evitar que se procese el mismo pago dos veces 
@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService {
private final PaymentRepository paymentRepository;

public Optional <Payment> findByIdIdempotencyKey(String idempotencyKey){
if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
    return Optional.empty();
}
return paymentRepository.findByIdempotencyKey(idempotencyKey);
}

//validar que la calve de idempotencia sea valida


public void validateIdempotencyKey(String idempotencyKey){
if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
    throw new IllegalArgumentException("Idempotency key is required");
}

if (idempotencyKey.length() < 16 || idempotencyKey.length() > 64 ) {
    throw new IllegalArgumentException("Idempotency key must be between 16 and 64 characters.");
}

if (!idempotencyKey.matches("^[a-zA-Z0-9_-]+$")) {
    throw new IllegalArgumentException("Idempotency key contains invalid characters");
}
}

@Transactional(readOnly = true)
public boolean isDuplicateRequest(String idempotencyKey){
    Optional <Payment> existingPayment = findByIdIdempotencyKey(idempotencyKey);

    if (existingPayment.isPresent()) {
        Payment payment = existingPayment.get();
        log.warn("Duplicate payment request detected", idempotencyKey, payment.getId());

        return true;
    }

    return false;
}

public void logDuplicateAttempt(String idempotencyKey, Long userId){
    log.warn("Duplicate payment attempt", userId, idempotencyKey, LocalDateTime.now());
}


     
}



