package com.payments.microservices.msvc_payments.request;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.payments.microservices.msvc_payments.entities.PaymentMethod;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCreateRequest {

private Long userId;

private Long appointmentId;

private Long shopId;

private BigDecimal amount;

private String currency;

private PaymentMethod paymentMethod;

private String notes;

private String description;

@Size(max = 4, message = "Card last four cannot exceed 4 characters")
private String cardLastFour;


@JsonProperty("card_token")
private String cardToken;

@Size(max = 30, message = "Cardholder name cannot exceed 30 characters")
@JsonProperty("card_holder_name") 
private String cardHolderName;
    
@JsonProperty("card_holder_email")
@Size(max = 60, message = "Card holder email cannot exceed 60 characters")
private String cardHolderEmail;
    
@JsonProperty("card_holder_document_type")
private String cardHolderDocumentType; // "DNI", "CUIT", "PASSPORT"
    
@JsonProperty("card_holder_document_number")
private String cardHolderDocumentNumber;

private String paymentMethodId;

}
