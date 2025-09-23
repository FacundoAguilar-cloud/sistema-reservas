package com.payments.microservices.msvc_payments.request;

import java.math.BigDecimal;

public class DebitCardPaymentRequest extends PaymentRequest {
private String cardToken;

private String cardHolderName;

private String cardHolderEmail;

private String cardHolderDocumentType;

private String cardHolderDocumentNumber;

public DebitCardPaymentRequest(BigDecimal amount, String description){
    super(amount, description);
}
}
