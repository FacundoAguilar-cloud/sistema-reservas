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
    System.out.println("=== INICIO REGISTRO ===");
    System.out.println("Email a registrar: " + request.getEmail());


               try {
                System.out.println("Verificando si el usuario existe...");
                ResponseEntity <UserDto> existingUser = userClient.findByEmail(request.getEmail());
                System.out.println("Usuario encontrado: " + existingUser.getBody());
            if (existingUser != null) {
                throw new ResourceAlreadyExistExcp("User with this email already exists");
            }
        } catch (FeignException.NotFound e) {
            System.out.println("Usuario no existe, continuar con el registro");
        } catch (FeignException e) {
         System.err.println("Error Feign al verificar usuario:");
         System.err.println("Status: " + e.status());
         System.err.println("Message: " + e.getMessage());
         System.err.println("Content: " + e.contentUTF8());
         
            if (e.status() != 401) {
             throw new RuntimeException("Error checking the user existence" + e.getMessage());
         }
        }

        System.out.println("Creando objeto para el nuevo usuario");
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
        
        System.out.println("Objeto newUser creado: " + newUser.getEmail());

        try {
            System.out.println("Llamando al userClient para crear el nuevo usuario");
            ResponseEntity<ApiResponse> response = userClient.createNewUser(newUser);
             System.out.println("Respuesta recibida - Status: " + response.getStatusCode());
             System.out.println("Respuesta recibida - Body: " + response.getBody());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() !=null) {
                 return response.getBody();
            } else{
                return new ApiResponse("Registration failed", null); 
            }

           
        } catch (FeignException e) {
            System.err.println("=== ERROR FEIGN AL CREAR USUARIO ===");
            System.err.println("Status: " + e.status());
            System.err.println("Message: " + e.getMessage());
            System.err.println("Content: " + e.contentUTF8());
            System.err.println("Request: " + e.request());
            if (e.status() == 409) {
             throw new ResourceAlreadyExistExcp("User with this email already exist!");
           }
           
           return new ApiResponse("Unexpected error during registration", null); //este es el error que nos esta dando a la hora de intentar registrar el user
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

