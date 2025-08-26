package com.payments.microservices.msvc_payments.response;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.mercadopago.MercadoPagoConfig;
import com.payments.microservices.msvc_payments.providers.PaymentProvider;
import com.payments.microservices.msvc_payments.request.CardPaymentRequest;

import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j
public class MercadoPagoCardProvider implements PaymentProvider <CardPaymentRequest> {

 private final MercadoPagoConfig config;
 private final RestTemplate template;   
    
	@Override
	public boolean processPayment(CardPaymentRequest request) {
		// Implement payment processing logic here
		return false;
	}

	@Override
	public String getProviderName() {
		// Return the provider name
		return "MercadoPago";
	}

	@Override
	public boolean isAvailable() {
		// Implement logic to check if provider is available
		return true;
	}
}
