package com.payments.microservices.msvc_payments.processing;

import org.springframework.stereotype.Component;

import com.payments.microservices.msvc_payments.entities.Payment;
import com.payments.microservices.msvc_payments.entities.PaymentMethod;
import com.payments.microservices.msvc_payments.entities.PaymentProcessingResult;
import com.payments.microservices.msvc_payments.request.CreditCardPaymentRequest;
import com.payments.microservices.msvc_payments.response.PaymentProviderResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreditCardPaymentProcessor extends BasePaymentProcessor {@Override
    //aca iria  inyectado el provider de esa tarjeta de credito   

    public PaymentProcessingResult processPayment(Payment payment) {
       try {
         validatePayment(payment);

         CreditCardPaymentRequest request = buildCreaditCardRequest(payment);
         
         PaymentProviderResponse response;
       } catch (Exception e) {
        // TODO: handle exception
       }
    }

    @Override
    public PaymentMethod getSupportedMethod() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSupportedMethod'");
    }

    private CreditCardPaymentRequest buildCreaditCardRequest(Payment payment){
        return CreditCardPaymentRequest.builder()
        .transactionId(payment.getTransactionId())
        .amount(payment.getAmount())
        .currency(payment.getCurrency())
        .description(payment.getDescription())
        .customerId(payment.getCustomerId())
        .cardToken(payment.getCardToken()) //esto deberia venir tokenizado, crear un metodo
        .build(); 
    }

}
