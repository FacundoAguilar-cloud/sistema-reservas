package com.user.microservices.msvc_user.security;

import java.util.Set;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.user.microservices.msvc_user.commons.UserDto;
import com.user.microservices.msvc_user.entities.Role;
import com.user.microservices.msvc_user.entities.User;
import com.user.microservices.msvc_user.exceptions.ResourceAlreadyExistExcp;
import com.user.microservices.msvc_user.repositories.UserRepository;
import com.user.microservices.msvc_user.request.LoginRequest;
import com.user.microservices.msvc_user.request.RegisterRequest;
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
private final ModelMapper modelMapper;


public ApiResponse register(RegisterRequest request){
    System.out.println("=== INICIO REGISTRO ===");
    System.out.println("Email a registrar: " + request.getEmail());
            //verificamos si existe el usuario
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistExcp("User with this email already exists");
        }

        System.out.println("Creando objeto para el nuevo usuario");
        User newUser = new User();
        newUser.setFirstname(request.getFirstname());
        newUser.setLastname(request.getLastname());
        newUser.setEmail(request.getEmail());
        newUser.setPhoneNumber(request.getPhoneNumber());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
    

        Set<Role> userRoles = request.getRoles() != null && !request.getRoles().isEmpty()
        ? request.getRoles()
        : Set.of(Role.CLIENT);
        newUser.setRoles(userRoles);
        //guardo usuario
        User savedUser= userRepository.save(newUser);
        //transformo con DTO
        UserDto userDto = modelMapper.map(savedUser, UserDto.class);
        
        return new ApiResponse("User registered successfully", userDto);
    }
    public ApiResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                )
        );

        String token = jwtService.generateToken(authentication);

        LoginResponse loginResponse = new LoginResponse(token);
        return new ApiResponse("Login successful", loginResponse);
    }

       
}
