package com.payments.microservices.msvc_payments.security.filter;


import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.payments.microservices.msvc_payments.security.services.RateLimitService;


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
                log.warn("Rate limit exceeded for client." , clientIdentifier);
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Too many requests. Please try again later.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldApplyRateLimit(String path, String method){
          return (path.contains("/generate") || 
                path.contains("/process") || 
                path.contains("/create")) && 
               "POST".equals(method);
    }

    private String getClientIdentifier(HttpServletRequest request){
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // ✅ AGREGAR esta validación para cuando authentication es null
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getName())) {
            String userId = authentication.getName();
            log.debug("Using user ID for rate limiting: {}", userId);
            return "user:" + userId;
        }

        // ✅ Fallback a IP cuando no hay autenticación
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // Si viene con múltiples IPs, tomar la primera
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        log.debug("No authenticated user found, using IP for rate limiting: {}", ip);
        return "ip:" + ip;
    
    }

   

}
