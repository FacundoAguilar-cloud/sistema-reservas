package com.user.microservices.msvc_user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class newUserRequest {
@NotBlank(message = "El nombre es obligatorio")
@Size(max = 50, message = "El nombre no puede exceder los 50 caracteres")
private String firstname;
@NotBlank(message = "El nombre es obligatorio")
@Size(max = 50, message = "El apellido no puede exceder los 50 caracteres")
private String lastname;
@NotBlank(message = "El nombre es obligatorio")
@Email(message = "the email has to be valid, please try again")
private String email;

private String phoneNumber;

private String password; // por ahora la dejamos
}
