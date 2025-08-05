package com.payments.microservices.msvc_payments.exceptions;

public class PaymentNoyAllowedException extends RuntimeException{
public PaymentNoyAllowedException(String message){
    super(message);
}    

}
