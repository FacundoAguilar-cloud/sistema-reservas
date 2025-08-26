package com.payments.microservices.msvc_payments.request;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper = true)
public class CardPaymentRequest extends PaymentRequest {

private String cardToken;

private String cardHolderName;

private String cardHolderEmail;

private String cardHolderDocumentType;

private String cardHolderDocumentNumber;

public CardPaymentRequest(BigDecimal amount, String description){
    super(amount, description);
}

}
