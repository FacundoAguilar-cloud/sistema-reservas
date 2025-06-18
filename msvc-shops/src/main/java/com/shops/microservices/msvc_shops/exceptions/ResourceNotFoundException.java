package com.shops.microservices.msvc_shops.exceptions;

public class ResourceNotFoundException extends RuntimeException{
public ResourceNotFoundException(String message){
    super(message);
}
}
