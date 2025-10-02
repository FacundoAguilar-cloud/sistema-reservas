package com.payments.microservices.msvc_payments.processing;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.payments.microservices.msvc_payments.entities.Payment;
import com.payments.microservices.msvc_payments.entities.PaymentMethod;
import com.payments.microservices.msvc_payments.entities.PaymentProcessingResult;
import com.payments.microservices.msvc_payments.providers.MercadoPagoQRProvider;
import com.payments.microservices.msvc_payments.request.QRPaymentRequest;
import com.payments.microservices.msvc_payments.response.PaymentProviderResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Component
@RequiredArgsConstructor
@Slf4j
public class QRPaymentProcessor extends BasePaymentProcessor {
private final MercadoPagoQRProvider qrProvider;

    @Override
    public PaymentProcessingResult processPayment(Payment payment) {
       log.info("Processing QR payment.", payment.getId());

       try {
        validatePayment(payment);

        QRPaymentRequest request = buildQRRequest(payment);
        PaymentProviderResponse response = qrProvider.processPayment(request);
        if (response.isSuccess()) {
            return buildSuccessResult(response.getTransactionId(), response.getMessage());
        }
        else{
            return buildFailedResult(response.getMessage(), response.getErrorCode());
        }
       } catch (Exception e) {
        log.error("Error processing QR payment", e);
        return buildFailedResult("Processing error", e.getMessage());
       }
    }

    @Override
    public PaymentMethod getSupportedMethod() {
        return PaymentMethod.QR;
    }

    private QRPaymentRequest buildQRRequest(Payment payment){
            return QRPaymentRequest.builder()    
            .transactionId(payment.getTransactionId())
            .amount(payment.getAmount())
            .currency(payment.getCurrency())
            .description(payment.getDescription())
            .title(payment.getDescription())
            .externalReference(payment.getExternalReference())
            .customerId(payment.getUserId())
            .expirationMinutes(30) // 30 minutos por defecto
            .metaData(createQRMetadata(payment))
            .build();
    }

    private Map <String, String> createQRMetadata(Payment payment){
        Map <String, String> metadata = new HashMap<>();
        metadata.put("payment_id", payment.getId().toString());
        metadata.put("user_id", payment.getUserId().toString());
        metadata.put("appointment_id", payment.getAppointmentId().toString());
        metadata.put("shop_id", payment .getShopId().toString());
        metadata.put("payment_type", "qr");

        return metadata;

    }

}
