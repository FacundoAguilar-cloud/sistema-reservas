package com.payments.microservices.msvc_payments.providers;

import java.math.BigDecimal;    
import com.payments.microservices.msvc_payments.request.QRPaymentRequest;
import com.payments.microservices.msvc_payments.response.PaymentProviderResponse;

public interface QRPaymentProvider extends PaymentProvider<QRPaymentRequest> {
String generateQRCode(BigDecimal amount, String description);
PaymentProviderResponse checkQRPaymentStatus(String qrId);
PaymentProviderResponse cancelQRPayment(String qrId);



}
