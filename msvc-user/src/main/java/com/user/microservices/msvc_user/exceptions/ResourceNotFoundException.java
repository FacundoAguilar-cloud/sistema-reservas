package com.user.microservices.msvc_user.exceptions;

public class ResourceNotFoundException extends RuntimeException {
public ResourceNotFoundException(String message){
    super(message);
} 
}
