package com.payments.microservices.msvc_payments.exceptions;

public class PaymentProcessingException extends RuntimeException {
public PaymentProcessingException(String message){
    super(message);
}
}
