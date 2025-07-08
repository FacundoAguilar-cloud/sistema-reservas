package com.appointments.microservices.msvc_appoinments.client;

import org.springframework.stereotype.Component;

import com.appointments.microservices.msvc_appoinments.dto.UserDto;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public UserDto getUserById(Long id) {
       return null;
    }

}
