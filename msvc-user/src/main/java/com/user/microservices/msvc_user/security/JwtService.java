package com.user.microservices.msvc_user.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import lombok.RequiredArgsConstructor;
//tener en cuenta que este servicio del token solo se encarga de validar, la construccion del token como tal está en el msvc dedicado a la seguridad en general del proyecto
@Service
@RequiredArgsConstructor
public class JwtService {
@Value("${}") private String secret; //esto tambien lo tenemos que configurar

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
