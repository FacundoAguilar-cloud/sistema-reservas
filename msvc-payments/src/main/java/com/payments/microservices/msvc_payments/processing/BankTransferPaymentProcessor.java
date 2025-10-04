package com.payments.microservices.msvc_payments.processing;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.payments.microservices.msvc_payments.client.UserClient;
import com.payments.microservices.msvc_payments.entities.Payment;
import com.payments.microservices.msvc_payments.entities.PaymentMethod;
import com.payments.microservices.msvc_payments.entities.PaymentProcessingResult;
import com.payments.microservices.msvc_payments.providers.MercadoPagoBankTransferProvider;
import com.payments.microservices.msvc_payments.request.BankTransferPaymentRequest;
import com.payments.microservices.msvc_payments.response.PaymentProviderResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Component
@RequiredArgsConstructor
@Slf4j
public class BankTransferPaymentProcessor extends BasePaymentProcessor {
private final MercadoPagoBankTransferProvider transferProvider;
private final UserClient userClient;

    @Override
    public PaymentProcessingResult processPayment(Payment payment) {
       log.info("Processing bank transfer payment.", payment.getId());

       try {
        validatePayment(payment);

        BankTransferPaymentProcessor request = buildBankTransferRequest(payment);
        PaymentProviderResponse response = transferProvider.processPayment(request);
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
        return PaymentMethod.BANK_TRANSFER;
    }

    private BankTransferPaymentRequest buildBankTransferRequest(Payment payment){
            return BankTransferPaymentRequest.builder()    
            .transactionId(payment.getTransactionId())
            .amount(payment.getAmount())
            .currency(payment.getCurrency())
            .description(payment.getDescription())
            .externalReference(payment.getExternalReference())
            .payerEmail(requ)
            .customerId(payment.getUserId())
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


    private String getPayerEmail(Payment payment){
        if (payment.getCardHolderEmail() != null) {
            return payment.getCardHolderEmail();
        }
        return userClient.getUserById;
    }

}
