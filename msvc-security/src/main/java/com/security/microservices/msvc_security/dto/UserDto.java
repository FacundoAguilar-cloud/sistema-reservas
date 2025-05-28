package com.security.microservices.msvc_security.dto;

import java.util.Set;

import javax.management.relation.Role;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class UserDto {

private Long id;
@NotBlank
@Size(max = 30, message = "The name cannot have more than 30 characters") 
@Column(name =  "first_name")
private String firstname;

@NotBlank
@Size(max = 30, message = "the lastname cannot have more than 30 characters")
@Column(name =  "last_name")
private String lastname;

@NotBlank
@Email(message = "Email must be valid!")
@Column(unique = true, nullable = false)
private String email;

private String phoneNumber;

@NotBlank(message = "password is mandatory")
@Column(nullable = false)
private String password;

private Set <Role> roles;

}


