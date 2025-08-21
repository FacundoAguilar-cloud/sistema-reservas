package com.payments.microservices.msvc_payments.processing;

import com.payments.microservices.msvc_payments.entities.Payment;
import com.payments.microservices.msvc_payments.entities.PaymentMethod;
import com.payments.microservices.msvc_payments.entities.PaymentProcessingResult;

public interface PaymentProcessor {
PaymentProcessingResult processPayment(Payment payment);
PaymentMethod getSupportedMethod();
boolean supports(PaymentMethod method);
}
