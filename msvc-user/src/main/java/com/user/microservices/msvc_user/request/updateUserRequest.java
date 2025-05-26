package com.user.microservices.msvc_user.request;


import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class updateUserRequest {

@Size(max = 50, message = "The name cannot exceed 30 characters")
private String firstname;

@Size(max = 50, message = "The lastname cannot exceed 30 characters")
private String lastname;
}
