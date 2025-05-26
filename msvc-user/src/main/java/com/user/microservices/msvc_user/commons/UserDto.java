package com.user.microservices.msvc_user.commons;

import java.util.Set;

import com.user.microservices.msvc_user.entities.Role;

import lombok.Data;

@Data
public class UserDto {
private Long id;
private String firstname;
private String lastname;
private String email;
private String phoneNumber;
private String password;
private Set <Role> roles;

}
