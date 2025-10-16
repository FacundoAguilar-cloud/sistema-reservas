package com.payments.microservices.msvc_payments.security.filter;


import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.payments.microservices.msvc_payments.security.services.RateLimitService;

import ch.qos.logback.core.joran.conditional.IfAction;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {
    private final RateLimitService rateLimitService;
    
    
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (shouldApplyRateLimit(path, method)) {
            String clientIdentifier = getClientIdentifier(request);

            if (!rateLimitService.tryConsume(clientIdentifier)) {
                
            }
        }
    }

}
