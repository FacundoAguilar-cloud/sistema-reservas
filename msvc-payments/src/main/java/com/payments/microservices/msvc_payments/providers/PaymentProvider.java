package com.payments.microservices.msvc_payments.providers;

import com.payments.microservices.msvc_payments.response.PaymentProviderResponse;

public interface PaymentProvider <T> {
PaymentProviderResponse processPayment(T request);

String getProviderName();

boolean isAvailable();
}
