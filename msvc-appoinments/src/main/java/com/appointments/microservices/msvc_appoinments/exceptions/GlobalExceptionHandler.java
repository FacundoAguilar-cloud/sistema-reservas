package com.appointments.microservices.msvc_appoinments.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



@RestControllerAdvice
public class GlobalExceptionHandler {

@ExceptionHandler
 public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex){
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
}

@ExceptionHandler
public ResponseEntity<String> handleNotFound(ResourceAlreadyExistException ex){
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
}

@ExceptionHandler
public ResponseEntity <String> handleAppoint(AppointmentException ex){
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
}

@ExceptionHandler
public ResponseEntity <String> handleUnaServ(ServiceUnavailableException ex){
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
}

@ExceptionHandler
public ResponseEntity<String> handleBusExcep(BusinessException ex){
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
}

}
