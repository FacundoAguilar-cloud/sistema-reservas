package com.user.microservices.msvc_user.request;

import lombok.Data;

@Data
public class newUserRequest {
private String firstname;
private String lastname;
private String email;
private String phoneNumber;
private String password;
}
