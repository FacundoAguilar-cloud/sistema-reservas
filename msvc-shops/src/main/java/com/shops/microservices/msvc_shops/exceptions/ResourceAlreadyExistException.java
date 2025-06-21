package com.shops.microservices.msvc_shops.exceptions;

public class ResourceAlreadyExistException extends RuntimeException {
public ResourceAlreadyExistException(String message){
    super(message);
}
}
