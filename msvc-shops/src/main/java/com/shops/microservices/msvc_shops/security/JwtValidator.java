package com.shops.microservices.msvc_shops.security;


import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;




//esta clase se va a encargar de obtener datos que necesitemos del token asi como tambien de validarlo y extrarlo

@Configuration
public class JwtValidator  {
 @Value("${application.security.jwt.secret-key}")
    private String secret;
    
  
    
    public String getEmail(String token) { //vendria a ser como obtener el nombre del usuario
        return JWT.decode(token).getSubject();
    }
    
    public Long getIdFromToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaim("id").asLong();
    }
    
    public List<String> getRolesFromToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaim("roles").asList(String.class);
    }

     public boolean validateToken(String token){
    try {
        JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
        return true;
    } catch (JWTVerificationException e) {
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
