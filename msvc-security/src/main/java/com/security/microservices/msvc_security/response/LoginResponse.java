package com.security.microservices.msvc_security.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
private String jwtToken;
private Long userId;
private String email;




public LoginResponse(String jwtToken){
    this.jwtToken= jwtToken;
}

 public String getToken() {
        return jwtToken;
    }

    public void setToken(String token) {
        this.jwtToken = jwtToken;
    }


}
