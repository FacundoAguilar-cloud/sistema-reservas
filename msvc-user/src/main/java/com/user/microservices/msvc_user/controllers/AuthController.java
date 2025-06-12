package com.user.microservices.msvc_user.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.microservices.msvc_user.exceptions.ResourceAlreadyExistExcp;
import com.user.microservices.msvc_user.request.LoginRequest;
import com.user.microservices.msvc_user.request.RegisterRequest;
import com.user.microservices.msvc_user.response.ApiResponse;
import com.user.microservices.msvc_user.security.AuthService;
import com.user.microservices.msvc_user.security.UserDetailsServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
private final AuthService authService;
private final UserDetailsServiceImpl userDetailsServiceImpl;

@PostMapping("/register")
public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
    try {
        return ResponseEntity.ok(authService.register(request));
    } 
    catch(ResourceAlreadyExistExcp e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(null, e.getMessage()));
    }
    catch (IllegalArgumentException e) {
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



