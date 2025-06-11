package com.user.microservices.msvc_user.security;

import java.util.Set;
import java.util.stream.Collectors;

import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.user.microservices.msvc_user.commons.UserDto;
import com.user.microservices.msvc_user.entities.Role;
import com.user.microservices.msvc_user.exceptions.FeignException;
import com.user.microservices.msvc_user.exceptions.ResourceAlreadyExistExcp;
import com.user.microservices.msvc_user.repositories.UserRepository;
import com.user.microservices.msvc_user.request.LoginRequest;
import com.user.microservices.msvc_user.request.RegisterRequest;
import com.user.microservices.msvc_user.request.newUserRequest;
import com.user.microservices.msvc_user.response.ApiResponse;
import com.user.microservices.msvc_user.response.LoginResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
private final PasswordEncoder passwordEncoder;
private final AuthenticationManager authenticationManager;
private final JwtService jwtService;
private final UserRepository userRepository;


public ApiResponse register(RegisterRequest request){
    System.out.println("=== INICIO REGISTRO ===");
    System.out.println("Email a registrar: " + request.getEmail());


               try {
                System.out.println("Verificando si el usuario existe...");
                Optional <UserDto> existingUser = userRepository.findByEmail(request.getEmail());
                System.out.println("Usuario encontrado: " + existingUser.orElse(null));
            if (existingUser.isPresent()) {
                throw new ResourceAlreadyExistExcp("User with this email already exists");
            }
        } catch (FeignException e) {
            if (e.getStatus() == 404) {
                System.out.println("Usuario no existe, continuar con el registro");
            } else {
                System.err.println("Error Feign al verificar usuario:");
                System.err.println("Status: " + e.getStatus());
                System.err.println("Message: " + e.getMessage());
                System.err.println("Content: " + e.contentUTF8());
                if (e.getStatus() != 401) {
                    throw new RuntimeException("Error checking the user existence" + e.getMessage());
                }
            }
        }

        System.out.println("Creando objeto para el nuevo usuario");
        newUserRequest  newUser = new newUserRequest();
        newUser.setFirstname(request.getFirstname());
        newUser.setLastname(request.getLastname());
        newUser.setEmail(request.getEmail());
        newUser.setPhoneNumber(request.getPhoneNumber());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
    

        Set<Role> userRoles = request.getRoles() != null && !request.getRoles().isEmpty()
        ? request.getRoles()
        : Set.of(Role.ROLE_CLIENT);
        Set<String> roleNames = userRoles.stream()
            .map(Role::name)
            .collect(Collectors.toSet());
        newUser.setRoles(roleNames);
        
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
            System.err.println("Status: " + e.getStatus());
            System.err.println("Message: " + e.getMessage());
            System.err.println("Content: " + e.contentUTF8());
            System.err.println("Request: " + e.request());
            if (e.getStatus() == 409) {
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
