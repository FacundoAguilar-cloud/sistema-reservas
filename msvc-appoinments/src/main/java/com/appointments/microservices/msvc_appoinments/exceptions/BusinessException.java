package com.appointments.microservices.msvc_appoinments.exceptions;

public class BusinessException extends RuntimeException {
    public BusinessException(String message){
        super(message);
    }
}
