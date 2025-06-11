package com.user.microservices.msvc_user.exceptions;

public class FeignException extends RuntimeException {
public FeignException(String message){
    super(message);
}
}
