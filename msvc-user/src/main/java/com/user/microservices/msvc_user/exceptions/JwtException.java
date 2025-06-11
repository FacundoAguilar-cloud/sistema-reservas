package com.user.microservices.msvc_user.exceptions;

public class JwtException extends RuntimeException{
public JwtException (String message){
    super(message);
}
}
