package com.payments.microservices.msvc_payments.providers;

import java.math.BigDecimal;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.merchantorder.MerchantOrderClient;
import com.mercadopago.client.payment.PaymentClient;
import com.payments.microservices.msvc_payments.request.QRPaymentRequest;
import com.payments.microservices.msvc_payments.response.PaymentProviderResponse;

import jakarta.annotation.PostConstruct;

@Component
public class MercadoPagoQRProvider implements QRPaymentProvider {

private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MercadoPagoQRProvider.class);

    private String accesstokem;

    private String webhookUrl;

    private PaymentClient paymentClient;

    private MerchantOrderClient merchantOrderClient;

    @PostConstruct
    public void init(){
        MercadoPagoConfig.setAccessToken(accesstokem);
        this.paymentClient = new PaymentClient();
        this.merchantOrderClient = new MerchantOrderClient();
    }

    @Override
    public PaymentProviderResponse processPayment(QRPaymentRequest request) {
       return null;
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

    @Override
    public PaymentProviderResponse cancelQRPayment(String qrId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cancelQRPayment'");
    }

}
