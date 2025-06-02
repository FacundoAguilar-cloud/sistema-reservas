package com.security.microservices.msvc_security.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.security.microservices.msvc_security.request.LoginRequest;
import com.security.microservices.msvc_security.request.RegisterRequest;
import com.security.microservices.msvc_security.response.ApiResponse;
import com.security.microservices.msvc_security.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
private final AuthService authService;

@PostMapping("/register")
public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request) {
    try {
        return ResponseEntity.ok(authService.register(request));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse(null, "cannot register the user, bad credentials")); //ver traduccion y corregir mas tarde
    }
}

@PostMapping("/login")
public ResponseEntity <ApiResponse> login (@RequestBody LoginRequest requestL) {
    try {
        return ResponseEntity.ok(authService.login(requestL));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("cannot login the user", null)); //lo mismo acá
    }
    
    
}


}
