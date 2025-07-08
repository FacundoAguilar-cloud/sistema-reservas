package com.appointments.microservices.msvc_appoinments.exceptions;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message){
        super(message);
    }

}
