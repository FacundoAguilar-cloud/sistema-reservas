package com.security.microservices.msvc_security.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

@Service
public class JwtService {
@Value("${application.security.jwt.secret-key}") //aca hay que poner la clave del app.properties
private String secret;
@Value("${application.security.jwt.expiration}") //lo mismo aca
private Long expirationTime;


public String generateToken(String email){ //genera el token
    return JWT
    .create()
    .withSubject(email)
    .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
    .sign(Algorithm.HMAC256(secret));
}

public boolean validateToken(String token){
    try {
        JWT.require(Algorithm.HMAC256(secret)).build().verify(token); 
        return true;
    } catch (JWTVerificationException e) {
        return false;
    }
}

public String getEmail(String token){
    return JWT.decode(token).getSubject();
}


}
