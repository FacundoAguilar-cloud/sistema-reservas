package com.security.microservices.msvc_security.request;

import java.util.Set;

import com.security.microservices.msvc_security.entities.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
@NotBlank(message = "Firstname is mandatory.")
@Size(max = 50, message = "The firstname cannot exceed 30 characters.")
private String firstname;

@NotBlank(message = "Lastname is mandatory.")
@Size(max = 50, message = "The lastname cannot exceed 30 characters.")
private String lastname;

@NotBlank(message = "Email is mandatory.")
@Email( message = "Email must be valid.")
private String email;

private String phoneNumber;

@NotBlank(message = "Password is mandatory.")
@Size(min = 6, message = "The password must be at least 6 characters long.")
private String password;

private Set <Role> roles;
}
