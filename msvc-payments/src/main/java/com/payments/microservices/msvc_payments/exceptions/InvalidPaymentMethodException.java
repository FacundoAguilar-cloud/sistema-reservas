package com.payments.microservices.msvc_payments.exceptions;

public class InvalidPaymentMethodException extends RuntimeException {
public InvalidPaymentMethodException(String message){
    super(message);
}
}
