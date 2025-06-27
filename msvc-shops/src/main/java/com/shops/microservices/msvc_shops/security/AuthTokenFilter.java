package com.shops.microservices.msvc_shops.security;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {
private final JwtValidator jwtValidator;
    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request, 
        @NonNull HttpServletResponse response, 
        @NonNull FilterChain filterChain)
            throws ServletException, IOException {
            final String requestTokenHeader = request.getHeader("Authorization");

            String username = null;
            String jwtToken = null;

            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                jwtToken = jwtValidator.extractToken(requestTokenHeader);

                try {
                    username = jwtValidator.getEmail(jwtToken);
                } catch (Exception e) {
                    logger.warn("Cant extract username from token, please try again");

                }
            } else{
                logger.debug("JWT not found!");
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtValidator.validateToken(jwtToken)) {
                    List<String> roles = jwtValidator.getRolesFromToken(jwtToken);
                List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());

                // Crear el objeto de autenticación
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Establecer la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
                filterChain.doFilter(request, response);
            }

        
                
    

    
    
    
    
    
    
    
    
    
    
    private String parseJwt(HttpServletRequest request){
    String headerAuth= request.getHeader("Authorization");
    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer")) {
        return headerAuth.substring(7); //aca basicamente se remueve el bearer y se toma unicamente el token
    }
    return null;
}
}
