package com.shops.microservices.msvc_shops.exceptions;

public class UnauthorizedException extends RuntimeException {
public UnauthorizedException(String message){
    super(message);
}
}
