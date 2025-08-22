package com.payments.microservices.msvc_payments.providers;

import java.math.BigDecimal;

import com.payments.microservices.msvc_payments.request.CreditCardPaymentRequest;
import com.payments.microservices.msvc_payments.response.PaymentProviderResponse;

public interface CreditCardPaymentProvider extends PaymentProvider<CreditCardPaymentRequest> {
PaymentProviderResponse refundPayment(String transactionId, BigDecimal amount);
PaymentProviderResponse getPaymentStatus(String transactionId);
}
