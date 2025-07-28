package com.payments.microservices.msvc_payments.exceptions;

public class ResourceNotFoundException extends RuntimeException {
public ResourceNotFoundException(String message){
    super(message);
}
}
