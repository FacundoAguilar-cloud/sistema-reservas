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
import com.security.microservices.msvc_security.dto.UserDto;
import com.security.microservices.msvc_security.entities.Role;
import com.security.microservices.msvc_security.exceptions.ResourceAlreadyExistExcp;
import com.security.microservices.msvc_security.request.LoginRequest;
import com.security.microservices.msvc_security.request.RegisterRequest;
import com.security.microservices.msvc_security.response.ApiResponse;
import com.security.microservices.msvc_security.response.LoginResponse;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor
public class AuthService {
private final PasswordEncoder passwordEncoder;
private final AuthenticationManager authenticationManager;
private final JwtService jwtService;
private final UserClient userClient;

public ApiResponse register(RegisterRequest request){
               try {
            UserDto existingUser = userClient.findByEmail(request.getEmail());
            if (existingUser != null) {
                throw new ResourceAlreadyExistExcp("User with this email already exists");
            }
        } catch (FeignException.NotFound e) {
            // Usuario no existe, continuar
        } catch (FeignException e) {
            // Si es error 401, ignorar y continuar (el endpoint está protegido)
         if (e.status() != 401) {
             throw new RuntimeException("Error checking the user existence");
         }
        }

        //este seria solo para registrar usuarios normales, clientes
        newUserRequest  newUser = new newUserRequest();
        newUser.setFirstname(request.getFirstname());
        newUser.setLastname(request.getLastname());
        newUser.setEmail(request.getEmail());
        newUser.setPhoneNumber(request.getPhoneNumber());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
    

        Set <Role> userRoles = request.getRoles() !=null && !request.getRoles().isEmpty()
        ? request.getRoles()
        : Set.of(Role.ROLE_CLIENT);
        newUser.setRoles(userRoles);


        try {
            ResponseEntity<ApiResponse> response = userClient.createNewUser(newUser);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() !=null) {
                 return response.getBody();
            } else{
                return new ApiResponse("Registration failed", null); 
            }

           
        } catch (FeignException e) {
           if (e.status() == 409) {
             throw new ResourceAlreadyExistExcp("User with this email already exist!");
           }
           
           return new ApiResponse("Unexpected error during registration", null);
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

