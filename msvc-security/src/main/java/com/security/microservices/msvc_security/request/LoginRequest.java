package com.security.microservices.msvc_security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

@NotBlank(message = "Email is mandatory")
@Email(message = "Email must be valid")
private String email;

@NotBlank(message = "password is mandatory")
private String password;


}
