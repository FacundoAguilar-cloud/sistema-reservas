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
    @Value("/")
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

                String userId = decodedJWT.getSubject();

                List <String> roles = decodedJWT.getClaim("roles").asList(String.class);

                if (userId != null) {
                    List <SimpleGrantedAuthority> authorities = roles != null ?
                    roles.stream().map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
                    .collect(Collectors.toList())
                    :List.of();

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null,  authorities); 
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.info("User authenticated successfully with roles:" , userId, roles);
                }
            } catch (Exception e) {
                log.error("JWT validation failed", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }

    protected boolean noFilter(HttpServletRequest request){
        String path = request.getRequestURI();

         return path.startsWith("/api/webhooks") || 
               path.startsWith("/actuator/health");
    }

}
