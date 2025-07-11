package com.appointments.microservices.msvc_appoinments.security;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
      @NonNull  HttpServletRequest request, 
      @NonNull   HttpServletResponse response, 
      @NonNull   FilterChain filterChain)
            throws ServletException, IOException {
        //esto lo vamos a poder terminar cuando tengamos la clase que valida directamente el jwt
    }

}
