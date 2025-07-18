package com.appointments.microservices.msvc_appoinments.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.appointments.microservices.msvc_appoinments.dto.UserDto;

@FeignClient(name ="msvc-user")
public interface UserClient {

@GetMapping("/api/user/get-by-id/{userId}") //revisar
    UserDto getUserById(@PathVariable("userId") Long id);
    
}
