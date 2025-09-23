package com.payments.microservices.msvc_payments.request;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditCardPaymentRequest extends PaymentRequest {

private String transactionId;
private BigDecimal amount;
private String currency;
private String description;
private String externalReference;


private String cardToken;
private String cardHolderName;
private String cardHolderEmail;
private String cardHolderDocumentType;
private String cardHolderDocumentNumber;

private Long customerId;
private String paymentMethodId; //Tipo de red de tarjeta (visa, mastercard, etc)

public CreditCardPaymentRequest(BigDecimal amount, String description){
    super(amount, description);
}

}
