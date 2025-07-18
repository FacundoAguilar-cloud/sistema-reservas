package com.appointments.microservices.msvc_appoinments.client;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
      try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null) {
                    template.header("Authorization", authHeader);
                    System.out.println("Token agregado a llamada Feign: " + authHeader.substring(0, 20) + "...");
                }
            }
        } catch (Exception e) {
            System.out.println("Error al obtener token para Feign: " + e.getMessage());
        }
    }
    }
 //esto intercepta todas las llamadas feign, extrae el token y agrega el header

