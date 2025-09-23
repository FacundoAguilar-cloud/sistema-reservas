package com.payments.microservices.msvc_payments.processing;

import org.springframework.stereotype.Component;

import com.payments.microservices.msvc_payments.entities.Payment;
import com.payments.microservices.msvc_payments.entities.PaymentMethod;
import com.payments.microservices.msvc_payments.entities.PaymentProcessingResult;
import com.payments.microservices.msvc_payments.request.CreditCardPaymentRequest;
import com.payments.microservices.msvc_payments.response.MercadoPagoCardProvider;
import com.payments.microservices.msvc_payments.response.PaymentProviderResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreditCardPaymentProcessor extends BasePaymentProcessor {
    //aca iria  inyectado el provider de esa tarjeta de credito
    private final MercadoPagoCardProvider cardProvider;   

    public PaymentProcessingResult processPayment(Payment payment) {
       log.info("Processing credit card payment ID", payment.getId());
       
        try {
         validatePayment(payment);

         CreditCardPaymentRequest request = buildCreaditCardRequest(payment);
         
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

    @Override
    public PaymentMethod getSupportedMethod() {
        return PaymentMethod.CREDIT_CARD;
    }

    private CreditCardPaymentRequest buildCreaditCardRequest(Payment payment){
        return CreditCardPaymentRequest.builder()
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

    

}
