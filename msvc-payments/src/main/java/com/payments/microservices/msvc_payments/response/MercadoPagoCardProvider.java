package com.payments.microservices.msvc_payments.response;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

	@Value(value = "${MERCADOPAGO_ACCESS_TOKEN}") //el token de acceso que tambien va a estar en el app.properties
    private String accessToken;

	 private static final String PAYMENTS_URL = "https://api.mercadopago.com/v1/payments";
    

 private final MercadoPagoConfig config;
 private final RestTemplate template; 
    
 public MercadoPagoCardProvider(MercadoPagoConfig config, RestTemplate template){
	
	this.config = config;
	this.template = template;
 }


	@Override
	public PaymentProviderResponse processPayment(CardPaymentRequest request) {
		log.info("Processing payment for transaction", request.getPaymentType(), request.getTransactionId());
		try {

		 validateRequest(request);		
			//crear request para la API de MP
		 Map <String, Object> paymentData = buildMercadoPagoRequest(request);

		 HttpHeaders headers = new HttpHeaders();
		 headers.setContentType(MediaType.APPLICATION_JSON);
		 headers.setBearerAuth(MercadoPagoConfig.getAccessToken());

		 HttpEntity<Map<String, Object>> entity = new HttpEntity<>(paymentData, headers);

		
		 String apiUrl = "https://api.mercadopago.com/v1/payments"; 
		 ResponseEntity <Map> response = template.exchange(apiUrl,
		 HttpMethod.POST, entity, Map.class );

		 return processApiResponse(response.getBody(), request); //esto tmb va a tener que hacerse
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

	


	private PaymentProviderResponse processApiResponse(Map<String, Object> response, CardPaymentRequest request){
		if (response == null) {
			return PaymentProviderResponse.builder()
			.success(false)
			.errorCode("MP_NO_RESPONSE")
			.message("No response from Mercado Pago.")
			.build();
		}
		String status = (String) response.get("status");
		String statusDetail = (String) response.get("status_detail");
		Object idObj = response.get("id");

		if (idObj == null) {
			return PaymentProviderResponse.builder()
			.success(false)
			.errorCode("MP_NO_ID")
			.message("No transaction id received")
			.build();
		}

		String transactionId = idObj.toString();
		boolean isSuccessfull = "approved".equals(status);

		PaymentProviderResponse.PaymentProviderResponseBuilder builder = PaymentProviderResponse.builder()
		.success(isSuccessfull)
		.transactionId(transactionId)
		.status(status)
		.amount(request.getAmount())
		.currency(request.getCurrency())
		.processedAt(LocalDateTime.now());

		     switch (status) {
            case "approved":
                builder.message("Payment approved successfully");
                break;
            case "pending":
                builder.message("Payment pending: " + statusDetail);
                break;
            case "rejected":
                builder.message("Payment rejected: " + statusDetail);
                builder.errorCode("MP_REJECTED");
                break;
            case "cancelled":
                builder.message("Payment cancelled");
                builder.errorCode("MP_CANCELLED");
                break;
            default:
                builder.message("Unknown status: " + status);
                builder.errorCode("MP_UNKNOWN_STATUS");
        }
		
			   Map<String, String> metadata = new HashMap<>();
        if (response.containsKey("card")) {
            Map<String, Object> cardInfo = (Map<String, Object>) response.get("card");
            if (cardInfo.containsKey("last_four_digits")) {
                metadata.put("card_last_four", cardInfo.get("last_four_digits").toString());
            }
            if (cardInfo.containsKey("first_six_digits")) {
                metadata.put("card_first_six", cardInfo.get("first_six_digits").toString());
            }
        }
        
        return builder.metadata(metadata).build();
    }

	public PaymentProviderResponse refundPayment(String transactionId, BigDecimal amount){
		log.info("Processing refund for transaction ", transactionId, amount);

		try {
			String refundUrL = PAYMENTS_URL + "/" + transactionId + "/refunds";

			Map <String, Object> refundData = new HashMap<>();

			if (amount != null) {
				refundData.put("amount", amount);
			}

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(accessToken);

			HttpEntity <Map<String, Object>> entity = new HttpEntity<>(refundData, headers);

			ResponseEntity <Map> response = template.exchange(refundUrL, HttpMethod.POST, entity, Map.class);

			return processRefundResponse(response.getBody(), transactionId, amount);

		} catch (Exception e) {
			log.error("Error processing refund for transaction", transactionId, e);

			return PaymentProviderResponse.builder()
			.success(false)
			.errorCode("MP_REFUND_CODE")
			.message("Error processing refund" + e.getMessage())
			.transactionId(transactionId)
			.build();
		}
	}

	public PaymentProviderResponse getRefundStatus(String refundId){
		log.info("Getting status from refund.", refundId);

		try {
			String statusUrl = PAYMENTS_URL + "/refunds/" + refundId;

			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(accessToken);

			HttpEntity <Void> entity = new HttpEntity<>(headers);

			ResponseEntity <Map> response = template.exchange(statusUrl, HttpMethod.GET, entity, Map.class);

			return processRefundResponse(response.getBody(), refundId, null);

		} catch (Exception e) {
			log.error("Error getting status from refund", refundId, e);

			return PaymentProviderResponse .builder()
			.success(false)
			.errorCode("MP_REFUND_STATUS_ERROR")
			.message("Error getting refund" + e.getMessage())
			.transactionId(refundId)
			.build();
		}
	}

	 private PaymentProviderResponse processRefundResponse(Map<String, Object> response, 
                                                         String transactionId, 
                                                         BigDecimal requestedAmount) {
        if (response == null) {
            return PaymentProviderResponse.builder()
                .success(false)
                .errorCode("MP_NO_RESPONSE")
                .message("No response from MercadoPago")
                .build();
        }
        
        String status = (String) response.get("status");
        Object idObj = response.get("id");
        Object amountObj = response.get("amount");
        
        String refundId = idObj != null ? idObj.toString() : null;
        BigDecimal refundedAmount = amountObj != null ? 
            new BigDecimal(amountObj.toString()) : requestedAmount;
        
        boolean isSuccessful = "approved".equals(status);
        
        Map<String, String> metadata = new HashMap<>();
        metadata.put("original_transaction_id", transactionId);
        metadata.put("refund_id", refundId);
        
        return PaymentProviderResponse.builder()
            .success(isSuccessful)
            .transactionId(refundId != null ? refundId : transactionId)
            .status(status)
            .amount(refundedAmount)
            .processedAt(LocalDateTime.now())
            .message(isSuccessful ? "Refund approved" : "Refund rejected")
            .metadata(metadata)
            .build();
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

	private void validateRequest(CardPaymentRequest request){
		if (request.getCardToken() == null|| request.getCardToken().trim().isEmpty()) {
			throw new IllegalArgumentException("Card token is required");
		}

		if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Amount must be greater than zero");
		}

		if (request.getCardHolderEmail() == null || request.getCardHolderEmail().trim().isEmpty() )  {
			throw new IllegalArgumentException("Cardholder email is required");
		}

		if (request.getCardHolderDocumentNumber() == null || request.getCardHolderDocumentNumber().trim().isEmpty()) {
			throw new IllegalArgumentException("Document number is required");
		}
	}




	


	
}




