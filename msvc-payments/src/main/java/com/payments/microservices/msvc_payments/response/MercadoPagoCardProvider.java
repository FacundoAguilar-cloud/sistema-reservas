package com.payments.microservices.msvc_payments.response;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.mercadopago.MercadoPagoConfig;
import com.payments.microservices.msvc_payments.entities.PaymentMethod;
import com.payments.microservices.msvc_payments.providers.PaymentProvider;
import com.payments.microservices.msvc_payments.request.CardPaymentRequest;

import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j
public class MercadoPagoCardProvider implements PaymentProvider <CardPaymentRequest> {

 private final MercadoPagoConfig config;
 private final RestTemplate template; 
    
 public MercadoPagoCardProvider(MercadoPagoConfig config, RestTemplate template){
	
	this.config = config;
	this.template = template;
 }


	@Override
	public PaymentProviderResponse processPayment(CardPaymentRequest request) {
		try {
			//crear request para la API de MP
		Map <String, Object> paymentData = buildMercadoPagoRequest(request);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(MercadoPagoConfig.getAccessToken());

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(paymentData, headers);

		
		String apiUrl = "https://api.mercadopago.com/v1/payments"; 
		ResponseEntity <Map> response = template.exchange(apiUrl,
		HttpMethod.POST, entity, Map.class );

		return processApiResponse(response.getBody()); //esto tmb va a tener que hacerse
		} catch (Exception e) {
			log.error("Error proccessing pay with card", e.getMessage());
		}
		return null;
		
	}

	private Map <String, Object> buildMercadoPagoRequest(CardPaymentRequest request){
		Map <String, Object> paymentData = new 	HashMap<>();
		paymentData.put("transaction_amount", request.getAmount());
        paymentData.put("token", request.getCardToken());
        paymentData.put("description", request.getDescription());
        paymentData.put("external_reference", request.getExternalReference());

		//tipo de pago
		Map <String, Object> paymentMethod = new HashMap<>();
		paymentMethod.put("id", "visa");
		paymentData.put("payment_method_id", paymentMethod);

		//info del que paga
		Map <String, Object> payer = new HashMap<>();
		payer.put("email", request.getCardHolderEmail());
		//DNI del que paga
		Map <String, Object> identification = new HashMap<>();
		identification.put("type", request.getCardHolderDocumentType());
		identification.put("number", request.getCardHolderDocumentNumber());

		paymentData.put("payer", payer);

		return paymentData;
	}

	


	private PaymentProviderResponse processApiResponse(Map<String, Object> response){
		String status = (String) response.get("status");
	}




	@Override
	public String getProviderName() {
		return "MERCADOPAGO_CARD"; //Puede ser cualquier tarjeta que este asociada dentro de MP.
	}

	@Override
	public boolean isAvailable() {
		// Implement logic to check if provider is available
		return true;
	}



	public boolean supportsPaymentMethod(PaymentMethod paymentMethod) {
		return paymentMethod == PaymentMethod.CREDIT_CARD || paymentMethod == PaymentMethod.DEBIT_CARD;
	}




	


	
}




