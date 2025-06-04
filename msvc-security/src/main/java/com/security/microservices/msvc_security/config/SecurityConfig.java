package com.security.microservices.msvc_security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.security.microservices.msvc_security.service.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
private final UserDetailsServiceImpl uds;

@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
    http.csrf(csrf -> csrf.disable())
    .httpBasic(Customizer.withDefaults()).authorizeHttpRequests(auth -> auth
    .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll().anyRequest().authenticated())
    .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
   // .exceptionHandling(excep -> excep.authenticationEntryPoint(null))//aca iria la clase que se encargará de ver si ese request fue autenticado o no
   // .addFilterBefore(null, UsernamePasswordAuthenticationToken.class);  //aca deberia de ir el authTokenFilter que todavia no tenemos
    return http.build();
}



@Bean
public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
}


@Bean
public AuthenticationManager authManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
}


}



