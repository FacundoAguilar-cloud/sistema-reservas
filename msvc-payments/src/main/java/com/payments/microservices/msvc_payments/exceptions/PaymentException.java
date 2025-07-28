package com.payments.microservices.msvc_payments.exceptions;

public class PaymentException extends RuntimeException {
public PaymentException(String message){
    super(message);
}    

}
