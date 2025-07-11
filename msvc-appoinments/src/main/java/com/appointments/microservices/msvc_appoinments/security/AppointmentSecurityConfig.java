package com.appointments.microservices.msvc_appoinments.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity

public class AppointmentSecurityConfig {
//aca necesitamos el authentrypoint y el authtokenfilter(HACER)

@Bean
public SecurityFilterChain filterChain(HttpSecurity http)throws Exception {
http.csrf(csrf -> csrf.disable()).httpBasic(Customizer.withDefaults()).authorizeHttpRequests(auth -> auth
 //Endpoints publicos   
.requestMatchers(HttpMethod.GET, "/api/appointment/get-by-id").permitAll()
.requestMatchers(HttpMethod.GET, "/api/appointment/all").permitAll()
.requestMatchers(HttpMethod.GET, "/api/appointment/by-client").permitAll()
.requestMatchers(HttpMethod.GET, "/api/appointment/by-shop").permitAll()
.requestMatchers(HttpMethod.GET, "/api/appointment/by-barber").permitAll()
.requestMatchers(HttpMethod.GET, "/api/appointment/by-status").permitAll()
.requestMatchers(HttpMethod.GET, "/api/appointemnt/by-date-range").permitAll()
.requestMatchers(HttpMethod.POST, "/api/appointment/create").permitAll()

//Endpoints privados
.requestMatchers(HttpMethod.PUT, "/api/appointment/update/**").hasAuthority("SHOP_OWNER")
.requestMatchers(HttpMethod.DELETE, "/api/appointment/delete/**").hasAnyAuthority("SHOP_OWNER")
.anyRequest().authenticated()
)
.sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS));
//aca falta el authEntryPoint y el authTokenFilter(AGREGAR)

return http.build();
}
}
