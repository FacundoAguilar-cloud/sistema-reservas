package com.security.microservices.msvc_security.service;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.security.microservices.msvc_security.client.UserClient;
import com.security.microservices.msvc_security.commons.newUserRequest;
import com.security.microservices.msvc_security.entities.RoleName;
import com.security.microservices.msvc_security.exceptions.ResourceAlreadyExistExcp;
import com.security.microservices.msvc_security.request.LoginRequest;
import com.security.microservices.msvc_security.request.RegisterRequest;
import com.security.microservices.msvc_security.response.ApiResponse;
import com.security.microservices.msvc_security.response.LoginResponse;

import feign.FeignException.FeignClientException;
import lombok.RequiredArgsConstructor;

@Service @RequiredArgsConstructor
public class AuthService {
private final PasswordEncoder passwordEncoder;
private final AuthenticationManager authenticationManager;
private final JwtService jwtService;
private final UserClient userClient;

public ApiResponse register(RegisterRequest request){
        if (userClient.findByEmail(request.getEmail()) != null) {
            throw new ResourceAlreadyExistExcp("User with this email already exists");
        }

        newUserRequest  newUser = new newUserRequest();
        newUser.setFirstname(request.getFirstname());
        newUser.setLastname(request.getLastname());
        newUser.setEmail(request.getEmail());
        newUser.setPhoneNumber(request.getPhoneNumber());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRoles(Set.of(RoleName.ROLE_CLIENT));

        try {
            ResponseEntity<ApiResponse> response = userClient.createNewUser(newUser);
            return response.getBody();
        } catch (FeignClientException e) {
            throw new ResourceAlreadyExistExcp("User creation failed in user service");
        }
    }

    public ApiResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword()
            )
        );

        String token = jwtService.generateToken(authentication.getName());

        LoginResponse loginResponse = new LoginResponse(token);
        return new ApiResponse("Login successful", loginResponse);
    }  //ver si esto esta ok, sino modificar (IMPORTANTE)


 }

