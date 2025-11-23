package com.payments.microservices.msvc_payments.security.filter;



import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Value("${application.security.jwt.secret-key}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
         String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();

                DecodedJWT decodedJWT = jwtVerifier.verify(token);

                Long userId = decodedJWT.getClaim("id").asLong();
                String userEmail = decodedJWT.getClaim("email").asString();
                List<String> roles = decodedJWT.getClaim("roles").asList(String.class);

                log.info(" JWT Filter - User ID extracted: {}", userId);
                log.info(" JWT Filter - User Email extracted: {}", userEmail);
                log.info(" JWT Filter - Roles extracted: {}", roles);

                if (userId != null) {
                    List<SimpleGrantedAuthority> authorities = roles != null ?
                    roles.stream().map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
                    .collect(Collectors.toList())
                    : List.of();

                    
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(userId.toString(), null, authorities); 
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.info(" JWT Filter - User authenticated successfully: ID={}, Email={}, Roles={}", 
                             userId, userEmail, roles);
                } else {
                    log.error(" JWT Filter - User ID is null in token");
                }
            } catch (Exception e) {
                log.error(" JWT Filter - Token validation failed: {}", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    
    }

    protected boolean shouldNotFilter (HttpServletRequest request){
        String path = request.getRequestURI();

         return path.startsWith("/api/webhooks") || 
               path.startsWith("/actuator/health");
              
    }

}
