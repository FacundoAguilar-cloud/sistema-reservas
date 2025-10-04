package com.payments.microservices.msvc_payments.providers;

import java.math.BigDecimal;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import com.payments.microservices.msvc_payments.entities.PaymentMethod;
import com.payments.microservices.msvc_payments.request.BankTransferPaymentRequest;
import com.payments.microservices.msvc_payments.response.PaymentProviderResponse;



@Component
public class MercadoPagoBankTransferProvider implements PaymentProvider<BankTransferPaymentRequest> {

    @Value("${MERCADOPAGO_ACCESS_TOKEN}")
    private String accesstoken;
    @Value("${WEBHOOK_BASE_URL:http://localhost:8003}/api/webhooks/mercadopago")
    private String webhookUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String PAYMENTS_URL =  "https://api.mercadopago.com/v1/payments";

  

    @Override
    public PaymentProviderResponse processPayment(BankTransferPaymentRequest request) {
     
      try {
        validateRequest(request);

        Map <String, Object> paymentData = buildMercadoPagoRequest(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accesstoken);
        headers.add("X-Idempotency-Key", request.getTransactionId());

        HttpEntity <Map<String, Object>> entity = new HttpEntity<>(paymentData, headers);

        //aca ya llamamos a MP
        ResponseEntity <Map> response = restTemplate.exchange(PAYMENTS_URL, HttpMethod.POST, entity, Map.class);

        return processApiResponse(response.getBody(), request);
        

      } catch (Exception e) {
        return PaymentProviderResponse.builder()
        .success(false)
        .errorCode("MP_TRANSFER_ERROR")
        .message("Error processing transfer payment" + e.getMessage())
        .amount(request.getAmount())
        .build();
      }
    }

  

   

    @Override
    public String getProviderName() {
        return "Mercado Pago Transfer";
    }

    @Override
    public boolean isAvailable() {
      return accesstoken != null && !accesstoken.trim().isEmpty();
    }

    

    private void validateRequest(BankTransferPaymentRequest request){
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be grater than zero.");
        }
        if (request.getPayerEmail() == null || request.getPayerEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Payer Email is required");
        }
        if (request.getPayerDocumentNumber() == null || request.getPayerDocumentNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Document number is mandatory.");
        }

    }
    

    private Map <String, Object>  buildMercadoPagoRequest(BankTransferPaymentRequest request){
        Map <String, Object> paymentData = new HashMap<>();

        paymentData.put("transaction_amoun", request.getAmount());
        paymentData.put("description", request.getDescription());
        paymentData.put("external_reference", request.getExternalReference());
        paymentData.put("payment_method_id", "pix"); //pix es para transfer instantanea dentro de ARG

        Map <String, Object> payer = new HashMap<>();
        payer.put("email", request.getPayerEmail());

        if (request.getPayerName() != null) {
            payer.put("first_name", request.getPayerName().split(" ")[0]);
            if (request.getPayerName().split( "").length > 1) {
                payer.put("last_name", request.getPayerName().substring(request.getPayerName().indexOf(" ") +1));
            }
        }

        Map <String, Object> identification = new HashMap<>();
        identification.put("type", request.getPayerDocumentType());
        identification.put("number", request.getPayerDocumentNumber());
        identification.put("identification", identification);

        paymentData.put("payer", payer);

        if (webhookUrl != null) {
            paymentData.put("notification_url", webhookUrl);
        }

        if (request.getCallbackUrl() != null) {
            paymentData.put("callback_url", request.getCallbackUrl());
        }


        Map<String, Object> metadata = new HashMap<>();
        metadata.put("payment_type", "bank_transfer");
        if (request.getCustomerId() != null) {
            metadata.put("customer_id", request.getCustomerId().toString());
        }
        paymentData.put("metadata", metadata);


        return paymentData;

    }


    private PaymentProviderResponse processApiResponse(Map <String, Object> response, BankTransferPaymentRequest request){
        if (response == null) {
           return PaymentProviderResponse.builder() 
           .success(false)
           .errorCode("MP_NO_RESPONSE")
           .message("No response from MP")
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

        boolean isSuccessful ="approved".equals(status) || "pending".equals(status);

        PaymentProviderResponse.PaymentProviderResponseBuilder builder = PaymentProviderResponse.builder()
        .success(isSuccessful)
        .transactionId(transactionId)
        .status(status)
        .amount(request.getAmount())
        .currency(request.getCurrency())
        .processedAt(LocalDateTime.now());

        Map <String, String> metadata = new HashMap<>();

        if (response.containsKey("transaction_details")) {
            Map<String, Object> transactionDetails = (Map<String, Object>) response.get("transaction_details");

            if (transactionDetails != null && transactionDetails.containsKey("external_resource_url")) {
                String paymentUrl = (String) transactionDetails.get("external_resource_url");
                builder.paymentUrl(paymentUrl);
                metadata.put("payment_url", paymentUrl);
            }
            if (transactionDetails != null && transactionDetails.containsKey("financial_institution")) {
                metadata.put("bank", transactionDetails.get("financial_institution").toString());
            }
        }

        switch (status) {
            case "pending":
                builder.message("Bank transfer pending. Complete the transfer to confirm payment");
            case  "approved":
                builder.message("Bank transfer completed succesfully.");   

            case "rejected":
                builder.message("Bank transfer rejected" + statusDetail);
                builder.errorCode("MP_REJECTED");
            case "cencelled":
                builder.message("Bank transfer cancelled succesfully");
                builder.errorCode("MP_CANCELLED");    
                break;
        
            default:
                builder.message("Umknown status" + status);
                builder.errorCode("MP_UNKNOWN_STATUS");
        }

        return builder.metadata(metadata).build();
    }





    @Override
    public boolean supportsPaymentMethod(PaymentMethod paymentMethod) {
        return PaymentMethod.BANK_TRANSFER.equals(paymentMethod);
    }







    
}

