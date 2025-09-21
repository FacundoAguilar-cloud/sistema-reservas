package com.payments.microservices.msvc_payments.providers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    private String accesstoken;

    private String webhookUrl;

    private PaymentClient paymentClient;

    private MerchantOrderClient merchantOrderClient;

    @PostConstruct
    public void init(){
        MercadoPagoConfig.setAccessToken(accesstoken);
        this.paymentClient = new PaymentClient();
        this.merchantOrderClient = new MerchantOrderClient();
    }

    @Override
    public PaymentProviderResponse processPayment(QRPaymentRequest request) {
      logger.info("Processing QR Payment{}", request.getAmount());

      try {
        //esto es simulacion, aca iria la integracion con MP
        String simulatedTransactionId = "MP_" + System.currentTimeMillis();

        String simulatedQR = "https://www.mercadopago.com/qr/" + simulatedTransactionId;

        return PaymentProviderResponse.builder()
        .success(true)
        .transactionId(simulatedTransactionId)
        .qrCode(simulatedQR)
        .paymentUrl(simulatedQR)
        .status("PENDING")
        .message("QR generated correctly.")
        .amount(request.getAmount())
        .currency("ARS")
        .processedAt(LocalDateTime.now())
        .build();

      } catch (Exception e) {
        logger.error("Error processing QR payment.", e);
        return PaymentProviderResponse.builder()
        .success(false)
        .errorCode("MP_ERROR")
        .message("Error generating QR" + e.getMessage())
        .build();
      }
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
        QRPaymentRequest request = new QRPaymentRequest(amount, description);
        PaymentProviderResponse response = processPayment(request);
        return response.isSuccess() ? response.getQrCode() : null;
    }

    @Override
    public PaymentProviderResponse checkQRPaymentStatus(String qrId) {
        return PaymentProviderResponse.builder()
        .success(true)
        .transactionId(qrId)
        .status("PENDING")
        .message("Status queried correctly. ")
        .build();
    }

    @Override
    public PaymentProviderResponse cancelQRPayment(String qrId) {
       return PaymentProviderResponse.builder()
       .success(true)
       .transactionId(qrId)
       .status("CANCELLED")
       .message("QR code successfully cancelled")
       .build();
    }

    @Override
    public boolean supportsPaymentMethod() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'supportsPaymentMethod'");
    }

}
