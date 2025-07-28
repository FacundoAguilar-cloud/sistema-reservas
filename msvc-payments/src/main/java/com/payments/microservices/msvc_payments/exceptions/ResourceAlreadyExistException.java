package com.payments.microservices.msvc_payments.exceptions;

public class ResourceAlreadyExistException extends RuntimeException {
public ResourceAlreadyExistException(String message){
    super(message);
}
}
