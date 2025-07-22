package com.appointments.microservices.msvc_appoinments.security;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.annotation.PostConstruct;

@Configuration
public class JwtValidator {
    
@Value("${application.security.jwt.secret-key}")
    private String secret;   

    @PostConstruct
    public void debugConfig() {
        System.out.println("=== CONFIGURACIÓN JWT ===");
        System.out.println("Secret key: " + (secret != null ? "Configurada (longitud: " + secret.length() + ")" : "NO CONFIGURADA"));
        System.out.println("Primeros 10 caracteres: " + (secret != null ? secret.substring(0, Math.min(10, secret.length())) : "N/A"));
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

    public boolean validateToken(String token){
        try {
            System.out.println("=== VALIDACIÓN JWT DEBUG ===");
            System.out.println("Secret key configurada: " + (secret != null ? "SÍ (longitud: " + secret.length() + ")" : "NO"));
            
            // Decodifica sin validar para ver el contenido
            DecodedJWT decodedJWT = JWT.decode(token);
            System.out.println("Algorithm del token: " + decodedJWT.getAlgorithm());
            System.out.println("Token expira en: " + decodedJWT.getExpiresAt());
            System.out.println("Token emitido en: " + decodedJWT.getIssuedAt());
            System.out.println("Tiempo actual: " + new Date());
            
            // Intenta validar
            JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
            System.out.println("✓ Token validado correctamente");
            return true;
        } catch (JWTVerificationException e) {
            System.out.println("✗ Error de validación: " + e.getClass().getSimpleName());
            System.out.println("✗ Mensaje: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public String extractToken(String bearerToken){
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}