package com.user.microservices.msvc_user.config;

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

import lombok.RequiredArgsConstructor;
import lombok.Value;
@Configuration
@EnableWebSecurity
public class SecurityConfig {
//private final UserDetailsServiceI uds;
//private final AuthTokenFilter authTokenFilter;
//private final AuthenticationEntryPointImpl authenticationEntryPointImpl;

@org.springframework.beans.factory.annotation.Value("${application.security.jwt.secret-key}")
private String secret;

@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
    http.csrf(csrf -> csrf.disable())
    .httpBasic(Customizer.withDefaults()).authorizeHttpRequests(auth -> auth
    .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
     .requestMatchers(HttpMethod.POST, "/api/user/create").permitAll()
    .requestMatchers(HttpMethod.GET,"/api/user/search-email/**").permitAll()
    .requestMatchers("/api/user/**").permitAll() //por ahora quedaria asi para pruebas 
    .anyRequest().authenticated())
    .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    //.exceptionHandling(excep -> excep.authenticationEntryPoint(authenticationEntryPointImpl))//aca iria la clase que se encargará de ver si ese request fue autenticado o no
    //.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
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
