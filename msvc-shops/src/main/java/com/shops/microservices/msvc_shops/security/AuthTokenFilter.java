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
         System.out.println("=== JWT FILTER DEBUG ===");
         System.out.println("Request URL: " + request.getRequestURL());
         System.out.println("Request Method: " + request.getMethod());
          
          String jwt = parseJwt(request);

          System.out.println("JWT Token extraído: " + (jwt != null ? "SÍ (longitud: " + jwt.length() + ")" : "NO"));    
          
           if (jwt != null) {
            System.out.println("Primeros 20 caracteres del token: " + jwt.substring(0, Math.min(20, jwt.length())));
            }

            try {
            if (jwt != null && jwtValidator.validateToken(jwt))  {
            System.out.println("Token válido: SÍ");

            String email = jwtValidator.getEmail(jwt);
            System.out.println("Email extraído: " + email);
            
           
            List<String> roles = jwtValidator.getRolesFromToken(jwt); 
             System.out.println("Roles extraídos: " + roles);

            List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
                System.out.println("Authorities creadas: " + authorities);
            
           
            UserDetails userDetails = User.builder()
                .username(email)
                .password("") 
                .authorities(authorities)
                .build();
            
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("Autenticación establecida correctamente");
            System.out.println("Usuario autenticado: " + SecurityContextHolder.getContext().getAuthentication().getName());
            System.out.println("Authorities en contexto: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            
        } else {
            System.out.println("Token inválido o nulo");
            if (jwt != null) {
                System.out.println("Razón: Token no pasa validación");
            }
        

        }
    } catch (Exception e) {
       System.out.println("ERROR en filtro JWT: " + e.getMessage());
        e.printStackTrace();
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
