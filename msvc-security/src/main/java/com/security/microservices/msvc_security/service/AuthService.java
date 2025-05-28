package com.security.microservices.msvc_security.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.security.microservices.msvc_security.client.UserClient;

import lombok.RequiredArgsConstructor;

@Service @RequiredArgsConstructor
public class AuthService {
private final PasswordEncoder passwordEncoder;
private final AuthenticationManager authenticationManager;
private final JwtService jwtService;
private final UserClient userClient;

}
