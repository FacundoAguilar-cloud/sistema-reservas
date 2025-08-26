package com.payments.microservices.msvc_payments.response;

import com.payments.microservices.msvc_payments.entities.PaymentMethod;
import com.payments.microservices.msvc_payments.request.PaymentRequest;

public interface PaymentProvider <T extends PaymentRequest> {

PaymentProviderResponse processPayment(T request);

String getProviderName();

boolean supportsPaymentMethod(PaymentMethod paymentMethod);
    
}
