package com.security.microservices.msvc_security.service;


import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.security.microservices.msvc_security.client.UserClient;
import com.security.microservices.msvc_security.dto.UserDto;


@Service 
public class UserDetailsServiceImpl implements UserDetailsService {
private final UserClient userClient;
    
    public UserDetailsServiceImpl (UserClient userClient){
        this.userClient = userClient;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    try {
        UserDto user = userClient.findByEmail(email);
        List<GrantedAuthority> authorities = user.getRoles().stream()
        .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
        .collect(java.util.stream.Collectors.toList());
        return new User(
            user.getEmail(),
            user.getPassword(),
            authorities
        );
    } catch (Exception e) {
       throw new UsernameNotFoundException("Username not found: " + email);
    }
    }

}
