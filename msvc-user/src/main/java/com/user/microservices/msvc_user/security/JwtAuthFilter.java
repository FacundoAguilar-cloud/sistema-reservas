package com.user.microservices.msvc_user.security;

import java.io.IOException;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthFilter extends OncePerRequestFilter {
private final String secret;
public JwtAuthFilter(String secret){this.secret = secret;}
    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request, 
        @NonNull HttpServletResponse response, 
        @NonNull FilterChain filterChain)
            throws ServletException, IOException {
         String header = request.getHeader("Authorization");
    if (header!=null && header.startsWith("Bearer ")) {
      try {
        String token = header.substring(7);
        String username = JWT.require(Algorithm.HMAC256(secret)).build()
            .verify(token).getSubject();
        if (username!=null) {
          UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(username, null, List.of());
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      } catch (Exception e) {
        // invalid token
      }
    }
    filterChain.doFilter(request, response);
  }
}
    


