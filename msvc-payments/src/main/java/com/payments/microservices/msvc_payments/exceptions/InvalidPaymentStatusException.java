package com.payments.microservices.msvc_payments.exceptions;

public class InvalidPaymentStatusException extends RuntimeException {
public InvalidPaymentStatusException(String message){
    super(message);
}
}
