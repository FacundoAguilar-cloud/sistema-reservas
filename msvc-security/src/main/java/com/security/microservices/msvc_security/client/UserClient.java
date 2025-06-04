package com.security.microservices.msvc_security.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.security.microservices.msvc_security.commons.newUserRequest;
import com.security.microservices.msvc_security.dto.UserDto;
import com.security.microservices.msvc_security.response.ApiResponse;


@FeignClient(name = "MSVC-USER")
public interface UserClient {
 
@GetMapping("/api/user/search-email/{email}")
UserDto findByEmail(@PathVariable String email);

@PostMapping("/api/user/create")
ResponseEntity<ApiResponse> createNewUser(@RequestBody newUserRequest request);


}
 