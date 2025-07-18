package com.appointments.microservices.msvc_appoinments.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class AppointmentSecurityConfig {
//aca necesitamos el authentrypoint y el authtokenfilter(HACER)
private final AuthEntryPoint authEntryPoint;
private final AuthTokenFilter authTokenFilter;
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	http
		.csrf(csrf -> csrf.disable())
		.httpBasic(Customizer.withDefaults())
		.authorizeHttpRequests(auth -> auth
			//Endpoints realmente públicos, o sea, sin auth:
			.requestMatchers(HttpMethod.GET, "/api/appointment/all").permitAll()
			.requestMatchers(HttpMethod.GET, "/api/appointment/by-status").permitAll()

			// Endpoints publicos con auth
			.requestMatchers(HttpMethod.GET, "/api/appointment/get-by-id").authenticated()
			.requestMatchers(HttpMethod.GET, "/api/appointment/by-client").authenticated()
			.requestMatchers(HttpMethod.GET, "/api/appointment/by-shop").authenticated()
			.requestMatchers(HttpMethod.GET, "/api/appointment/by-barber").authenticated()
			.requestMatchers(HttpMethod.GET, "/api/appointment/by-date-range").authenticated()
			.requestMatchers(HttpMethod.POST, "/api/appointment/create/**").authenticated()

			// Endpoints privados
			.requestMatchers(HttpMethod.PUT, "/api/appointment/update/**").hasAuthority("SHOP_OWNER")
			.requestMatchers(HttpMethod.DELETE, "/api/appointment/delete/**").hasAnyAuthority("SHOP_OWNER")
			.anyRequest().authenticated()
		)
		.sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
		.exceptionHandling(excep -> excep.authenticationEntryPoint(authEntryPoint))
		.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
	return http.build();
}
}
