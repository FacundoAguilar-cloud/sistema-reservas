package com.payments.microservices.msvc_payments.processing;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.payments.microservices.msvc_payments.entities.PaymentMethod;

@Configuration
public class PaymentProcessorConfig {

@Bean
public Map <PaymentMethod, PaymentProcessor> paymentProcessors(
    CreditCardPaymentProcessor creditCardPaymentProcessor,
    DebitCardPaymentProcessor debitCardPaymentProcessor,
    QRPaymentProcessor qrPaymentProcessor
){

Map<PaymentMethod, PaymentProcessor> processors = new HashMap<>();
processors.put(PaymentMethod.CREDIT_CARD, creditCardPaymentProcessor);
processors.put(PaymentMethod.DEBIT_CARD, debitCardPaymentProcessor);
processors.put(PaymentMethod.QR, qrPaymentProcessor);   

return processors;

}

@Bean
public RestTemplate restTemplate(){
    return new RestTemplate();
}

}
