package com.payments.microservices.msvc_payments.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.payments.microservices.msvc_payments.security.filter.JwtAuthenticationFilter;
import com.payments.microservices.msvc_payments.security.filter.RateLimitFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
@EnableWebSecurity

public class SecurityConfig {
private final JwtAuthenticationFilter jwtAuthenticationFilter;
private final RateLimitFilter rateLimitFilter;


@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity htpp) throws Exception{
    htpp.csrf(csrf -> csrf.disable())
    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    )
    .authorizeHttpRequests(auth -> auth.requestMatchers("/api/webhooks/**").permitAll()
    
    .requestMatchers("/actuator/health").permitAll()
    
    .requestMatchers("/api/payments/**").authenticated()

    .anyRequest().authenticated()
    
    )

    .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    

    return htpp.build();
}

}
