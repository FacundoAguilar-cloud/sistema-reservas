package com.shops.microservices.msvc_shops.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class ShopSecurityConfig {

@Bean
public SecurityFilterChain filterChain(HttpSecurity http)throws Exception {
return http.csrf(csrf ->csrf.disable())
.sessionManagement(session -> session
.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
.authorizeHttpRequests(auth -> auth

//endpoints publicos
.requestMatchers(HttpMethod.GET, "/api/shop/search").permitAll()
.requestMatchers(HttpMethod.GET, "/api/shop/get-by-id/**").permitAll()
.requestMatchers(HttpMethod.GET, "/api/shop/all-types").permitAll()

//endpoints privados:
.requestMatchers(HttpMethod.POST, "/api/shop/create").hasRole("SHOP_OWNER")
.requestMatchers(HttpMethod.PUT, "/api/shop/update/**").hasRole("SHOP_OWNER")
.requestMatchers(HttpMethod.DELETE, "/api/shop/delete/**").hasRole("SHOP_OWNER")
.requestMatchers(HttpMethod.GET, "/api/shop/get-by-owner").hasRole("SHOP_OWNER")

.anyRequest().authenticated()
)
.oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults())) 
.build();
}    

}
