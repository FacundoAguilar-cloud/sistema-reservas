package com.payments.microservices.msvc_payments.providers;

import java.math.BigDecimal;

import com.payments.microservices.msvc_payments.response.PaymentProviderResponse;

public interface RefundablePaymentProvider {
PaymentProviderResponse refundPayment(String transactionId, BigDecimal amount);

PaymentProviderResponse getRefundStatus(String refundId);
}
