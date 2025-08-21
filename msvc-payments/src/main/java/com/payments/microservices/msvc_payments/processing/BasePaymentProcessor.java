package com.payments.microservices.msvc_payments.processing;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.payments.microservices.msvc_payments.entities.Payment;
import com.payments.microservices.msvc_payments.entities.PaymentMethod;
import com.payments.microservices.msvc_payments.entities.PaymentProcessingResult;
import com.payments.microservices.msvc_payments.exceptions.InvalidPaymentMethodException;
import com.payments.microservices.msvc_payments.exceptions.PaymentException;

public abstract class BasePaymentProcessor implements PaymentProcessor {
@Override
public boolean supports(PaymentMethod method){
    return getSupportedMethod().equals(method);
}

protected PaymentProcessingResult buildSuccessResult(String transactionId, String providerResponse){
    return PaymentProcessingResult.builder()
    .success(true)
    .transactionId(transactionId)
    .providerResponse(providerResponse)
    .proccesedAt(LocalDateTime.now())
    .requiresConfirmation(false)
    .build();
}

protected PaymentProcessingResult buildFailedResult(String providerResponse, String errorMessage){
    return PaymentProcessingResult.builder()
    .success(false)
    .errorMessage(errorMessage)
    .providerResponse(providerResponse)
    .proccesedAt(LocalDateTime.now())
    .requiresConfirmation(false)
    .build();
}

protected void validatePayment(Payment payment){
    if (payment == null) {
        throw new PaymentException("Payment not found");
    }

    if (payment.getAmount() == null || payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
        throw new PaymentException("Invalid payment amount.");
    }
    if (!supports(payment.getPaymentMethod())) {
        throw new InvalidPaymentMethodException("Invalid payment method.");
    }
}


}
