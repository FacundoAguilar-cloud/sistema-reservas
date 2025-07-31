package com.payments.microservices.msvc_payments.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.payments.microservices.msvc_payments.dto.UserDto;

@FeignClient(name ="msvc-user")
public interface UserClient {

@GetMapping("/api/user/get-by-id/{userId}") // lo dejamos listo al igual que el ShopClient
    UserDto getUserById(@PathVariable("userId") Long id);
    
}

