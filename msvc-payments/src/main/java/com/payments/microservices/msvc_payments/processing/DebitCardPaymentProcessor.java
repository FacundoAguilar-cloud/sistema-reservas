package com.payments.microservices.msvc_payments.processing;

import org.codehaus.plexus.component.annotations.Component;

import com.payments.microservices.msvc_payments.entities.Payment;
import com.payments.microservices.msvc_payments.entities.PaymentMethod;
import com.payments.microservices.msvc_payments.entities.PaymentProcessingResult;
import com.payments.microservices.msvc_payments.request.CardPaymentRequest;
import com.payments.microservices.msvc_payments.response.MercadoPagoCardProvider;
import com.payments.microservices.msvc_payments.response.PaymentProviderResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Component(role = DebitCardPaymentProcessor.class)
@RequiredArgsConstructor
@Slf4j
public class DebitCardPaymentProcessor extends BasePaymentProcessor {
private final MercadoPagoCardProvider cardProvider; 
    @Override
    public PaymentProcessingResult processPayment(Payment payment) {
         log.info("Processing debit card payment ID", payment.getId());
       
        try {
         validatePayment(payment);

         CardPaymentRequest request = buildCardRequest (payment);
         
         PaymentProviderResponse response = cardProvider.processPayment(request);
         
         if (response.isSuccess()) {
            return buildSuccessResult(response.getTransactionId(), response.getMessage());
         } else{
            return buildFailedResult(response.getMessage(), response.getErrorCode());
         }

       } catch (Exception e) {
        log.error("Error processing credit card payment", e);
        return buildFailedResult("Processing error.", e.getMessage());
       }
    }

    private CardPaymentRequest buildCardRequest(Payment payment) {
        return CardPaymentRequest.builder()
        .transactionId(payment.getTransactionId())
       .amount(payment.getAmount())
        .currency(payment.getCurrency())
        .externalReference(payment.getExternalReference())
        .cardToken(payment.getCardToken())
        .cardHolderName(payment.getCardHolderName())
        .cardHolderEmail(payment.getCardHolderEmail())
        .cardHolderDocumentType("DNI")
        .cardHolderDocumentNumber(payment.getCardHolderDocumentNumber())
        .paymentMethodId(payment.getPaymentMethodId())
        .description(payment.getDescription())
        .customerId(payment.getCustomerId())
        .cardToken(payment.getCardToken()) //esto deberia venir tokenizado, crear un metodo
        .build(); 
    }



    @Override
    public PaymentMethod getSupportedMethod() {
        return PaymentMethod.DEBIT_CARD;
    }

}
