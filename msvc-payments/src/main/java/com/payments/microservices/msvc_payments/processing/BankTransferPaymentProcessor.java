package com.payments.microservices.msvc_payments.processing;



import org.springframework.stereotype.Component;


import com.payments.microservices.msvc_payments.dto.UserDto;
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
private final UserDataService userDataService;  

    @Override
    public PaymentProcessingResult processPayment(Payment payment) {
       log.info("Processing bank transfer payment.", payment.getId());

       try {
        validatePayment(payment);

        BankTransferPaymentRequest request = buildBankTransferRequest(payment);
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
            .payerEmail(getPayerEmail(payment))
            .payerName(getPayerName(payment))
            .payerDocumentType(getPayerDocumentType(payment))
            .payerDocumentNumber(getPayerDocumentNumber(payment))
            .customerId(payment.getUserId())
            .build();
    }

   


    private String getPayerEmail(Payment payment){
        if (payment.getCardHolderEmail() != null && !payment.getCardHolderEmail().trim().isEmpty())  {
            return payment.getCardHolderEmail();
        }

        String userEmail = userDataService.getUserEmail(payment.getUserId());
        if (userEmail != null) {
            return userEmail;
        }

        throw new IllegalArgumentException("Payer email is required for bank transfer, please provide it in the payment request");
    }

    private String getPayerName(Payment payment){
        if (payment.getCardHolderName() != null && !payment.getCardHolderName().trim().isEmpty()) {
            return payment.getCardHolderName();
        }
        UserDto user = userDataService.getUserData(payment.getUserId());
        if (user != null) {
            return user.getName();
        }
        return "Customer";
    }

    private String getPayerDocumentNumber(Payment payment){
        if (payment.getCardHolderDocumentNumber() != null 
        && !payment.getCardHolderDocumentNumber().trim().isEmpty()) {
            return payment.getCardHolderDocumentNumber();
        }
        UserDto user = userDataService.getUserData(payment.getUserId());
        if (user != null && user.getDocumentNumber() != null) {
            return user.getDocumentNumber();
        }
        throw new IllegalArgumentException("Document number is required for bank transfer");
    }

    private String getPayerDocumentType(Payment payment) {
        if (payment.getCardHolderDocumentType() != null) {
            return payment.getCardHolderDocumentType();
        }
        return "DNI";
    }

}
