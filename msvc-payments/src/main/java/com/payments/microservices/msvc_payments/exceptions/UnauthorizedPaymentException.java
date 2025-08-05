package com.payments.microservices.msvc_payments.exceptions;

public class UnauthorizedPaymentException extends RuntimeException {
public UnauthorizedPaymentException(String message){
    super(message);
}    

}
