package com.shops.microservices.msvc_shops.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.auth0.jwt.exceptions.JWTVerificationException;



@RestControllerAdvice
public class GlobalExceptionHandler {

@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex){
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
}

@ExceptionHandler(UnauthorizedException.class)
public ResponseEntity<String> handleNotFound(UnauthorizedException ex){
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
}

@ExceptionHandler(ResourceAlreadyExistException.class)
public ResponseEntity<String> handleNotFound(ResourceAlreadyExistException ex){
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
}
@ExceptionHandler(JWTVerificationException.class)
public ResponseEntity<String> handleJwtVeri(JWTVerificationException ex){
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
}

}
