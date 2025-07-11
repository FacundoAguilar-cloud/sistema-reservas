package com.shops.microservices.msvc_shops.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class ShopSecurityConfig {
//esto es lo que vamos a tener que rehacer dado que cambie las dependencias
private final AuthEntryPoint authEntryPoint; 
private final AuthTokenFilter authTokenFilter;
@Bean
public SecurityFilterChain filterChain(HttpSecurity http)throws Exception {
http.csrf(csrf -> csrf.disable())
	.httpBasic(Customizer.withDefaults())
	.authorizeHttpRequests(auth -> auth
		//endpoints publicos
		.requestMatchers(HttpMethod.GET, "/api/shop/search").permitAll()
		.requestMatchers(HttpMethod.GET, "/api/shop/get-by-id/**").permitAll()
		.requestMatchers(HttpMethod.GET, "/api/shop/all-types").permitAll()

		//endpoints privados:
		.requestMatchers(HttpMethod.POST, "/api/shop/create").hasAuthority("SHOP_OWNER") //probar si funciona y anotarlo directamente aca
		.requestMatchers(HttpMethod.PUT, "/api/shop/update/**").hasAnyAuthority("SHOP_OWNER")
		.requestMatchers(HttpMethod.DELETE, "/api/shop/delete/**").hasAuthority("SHOP_OWNER")
		.requestMatchers(HttpMethod.GET, "/api/shop/get-by-owner").hasAuthority("SHOP_OWNER")
		.anyRequest().authenticated()
	)
	.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	.exceptionHandling(excep -> excep.authenticationEntryPoint(authEntryPoint))
	.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

return http.build();
}

}
