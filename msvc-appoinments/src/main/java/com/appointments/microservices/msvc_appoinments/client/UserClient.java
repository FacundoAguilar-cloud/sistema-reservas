package com.appointments.microservices.msvc_appoinments.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.appointments.microservices.msvc_appoinments.dto.UserDto;

@FeignClient(name ="msvc-user")
public interface UserClient {

@GetMapping("/api/users/{id}") //esto obviamente lo tenemos que revisar pero por ahora va a quedar asi
    UserDto getUserById(@PathVariable("id") Long id);
    
}
