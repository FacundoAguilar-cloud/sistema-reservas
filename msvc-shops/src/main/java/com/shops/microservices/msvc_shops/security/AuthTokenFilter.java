package com.shops.microservices.msvc_shops.security;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
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
           // final String requestTokenHeader = request.getHeader("Authorization"); /ESTO LO COMENTAMOS POR EL MOMENTO 

          String jwt = parseJwt(request);
    try {
        if (jwt != null && jwtValidator.validateToken(jwt)) {
            String email = jwtValidator.getEmail(jwt);
            
            // Extraer roles directamente del JWT
            List<String> roles = jwtValidator.getRolesFromToken(jwt); // Necesitas crear este método
            
            List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
            
            // Crear un UserDetails simple sin consultar la BD
            UserDetails userDetails = User.builder()
                .username(email)
                .password("") // No necesitas password para JWT
                .authorities(authorities)
                .build();
            
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    } catch (Exception e) {
        logger.error("Cannot set user authentication: {}", e);
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
