package com.payments.microservices.msvc_payments.exceptions;

public class PaymentNotAllowedException extends RuntimeException{
public PaymentNotAllowedException(String message){
    super(message);
}    

}
