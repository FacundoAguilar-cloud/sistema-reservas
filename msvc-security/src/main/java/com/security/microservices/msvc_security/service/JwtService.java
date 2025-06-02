package com.security.microservices.msvc_security.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Service
public class JwtService {
@Value("${application.security.jwt.secret-key}") //aca hay que poner la clave del app.properties
private String secretJwt;
@Value("${application.security.jwt.expiration}") //lo mismo aca
private Long expirationTime;


public String generateToken(String email){
    return JWT
    .create()
    .withSubject(email)
    .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
    .sign(Algorithm.HMAC256(secretJwt));
}


}
