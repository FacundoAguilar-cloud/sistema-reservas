package com.user.microservices.msvc_user.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

@Value("${application.security.jwt.secret-key}") //esto hay que configurarlo mas tarde, recordar que la clave la ponemos nosotros (IMPORTANTE)
private String secret;

@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	JwtAuthFilter authFilter = new JwtAuthFilter(secret);
    http.csrf(csrf -> csrf.disable())
    .httpBasic(Customizer.withDefaults())
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/user//**").permitAll() //por ahora quedaria asi para pruebas 
        .requestMatchers("/api/user/search-email/**").permitAll()
        .anyRequest().authenticated()
    );

    http.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
    // Add further security configuration as needed
    
    return http.build();
}

}
