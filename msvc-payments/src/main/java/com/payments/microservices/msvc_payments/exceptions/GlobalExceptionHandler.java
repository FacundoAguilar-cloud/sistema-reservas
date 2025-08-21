package com.payments.microservices.msvc_payments.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

@ExceptionHandler
public ResponseEntity <String> handleNotFound(ResourceNotFoundException ex){
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
}

@ExceptionHandler
public ResponseEntity <String> handleAlreadyExist(ResourceAlreadyExistException ex){
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
}

@ExceptionHandler
public ResponseEntity <String> handlePaymentExcp(PaymentException ex){
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
}

@ExceptionHandler
public ResponseEntity <String> handlePaymentNotAllExcp(PaymentNotAllowedException ex){
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ex.getMessage());
}

@ExceptionHandler
public ResponseEntity <String> handleUnauthorizedPayExcp(UnauthorizedPaymentException ex){
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
}
@ExceptionHandler
public ResponseEntity <String> handlePaymentDataExcp(PaymentDataException ex){
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
}

@ExceptionHandler
public ResponseEntity <String> handlePaymentProcs(PaymentProcessingException ex){
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
}

@ExceptionHandler
public ResponseEntity <String> handleInvPayStatus(InvalidPaymentStatusException ex){
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
}

@ExceptionHandler
public ResponseEntity <String> handlePayMethod(InvalidPaymentMethodException ex){
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
}

}
