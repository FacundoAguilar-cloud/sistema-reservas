package com.payments.microservices.msvc_payments.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.payments.microservices.msvc_payments.security.filter.RateLimitFilter;
import lombok.RequiredArgsConstructor;


@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
@EnableWebSecurity

public class SecurityConfig {

private final RateLimitFilter rateLimitFilter;


@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity htpp) throws Exception{
    htpp.csrf(csrf -> csrf.disable())
    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    )
    .authorizeHttpRequests(auth -> auth.requestMatchers("/api/webhooks/**").permitAll()
    
    .requestMatchers("/actuator/health").permitAll()

    .requestMatchers("/api/webhooks").permitAll()
    
    .requestMatchers("/api/payments/**").authenticated()

    .anyRequest().authenticated()
    
    )

    .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
    .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class);
    

    return htpp.build();
}

 @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation("http://localhost:8080");
    }

}


