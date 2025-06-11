package com.user.microservices.msvc_user.security;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Service
public class JwtService {
@Value("${application.security.jwt.secret-key}") //aca hay que poner la clave del app.properties
private String secret;
@Value("${application.security.jwt.expiration}") //lo mismo aca
private Long expirationTime;


public String generateToken(Authentication authentication){ //genera el token
    UserAppDetails	userPrincipal = (UserAppDetails) authentication.getPrincipal();

    List <String> roles = userPrincipal.getAuthorities()
    .stream().
    
    map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    return JWT
    .create()
    .withSubject(userPrincipal.getEmail())
    .withClaim("id",userPrincipal.getId())
    .withArrayClaim("roles", roles.toArray(new String[0]))
    .withIssuedAt(new Date())
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
public Long getIdFromToken(String token){
    DecodedJWT jwt = JWT.decode(token);

    return jwt.getClaim("id").asLong();
}

public List <String> getRolesFromToken(String token){
    DecodedJWT jwt = JWT.decode(token);

    return jwt.getClaim("roles").asList(String.class);
}
}
