package com.payments.microservices.msvc_payments.providers;

import java.math.BigDecimal;

import com.payments.microservices.msvc_payments.request.QRPaymentRequest;
import com.payments.microservices.msvc_payments.response.PaymentProviderResponse;

public interface QRPaymentProvider extends PaymentProvider<QRPaymentRequest> {
String generateQRCode(BigDecimal amount, String description);
PaymentProviderResponse checkQRPaymentStatus(String qrId);

public class MercadoPagoQRProvider implements QRPaymentProvider {

    @Override
    public PaymentProviderResponse processPayment(QRPaymentRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processPayment'");
    }

    @Override
    public String getProviderName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProviderName'");
    }

    @Override
    public boolean isAvailable() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isAvailable'");
    }

    @Override
    public String generateQRCode(BigDecimal amount, String description) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateQRCode'");
    }

    @Override
    public PaymentProviderResponse checkQRPaymentStatus(String qrId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkQRPaymentStatus'");
    }

    
}
}
