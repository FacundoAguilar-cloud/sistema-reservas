package com.security.microservices.msvc_security.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.security.microservices.msvc_security.dto.UserDto;

@FeignClient(name = "msvc-user")
public interface UserClient {
 
@GetMapping("/api/user/search-email{email}")
UserDto findByEmail(@PathVariable("email") String email);

//aca vamos a tener que poner el metodo del existByEmail

}
 