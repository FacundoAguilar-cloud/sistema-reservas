package com.user.microservices.msvc_user.security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.user.microservices.msvc_user.entities.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserAppDetails implements UserDetails {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String password;
    private Collection <GrantedAuthority> authorities;

    public static UserAppDetails createUserDetails(User user){
        List <GrantedAuthority> authorities = user.getRoles().stream()
        .map(role -> new SimpleGrantedAuthority(role.name())).collect(Collectors.toList());//convertimos cada rol en un SGA de Spring Security
    
        return new UserAppDetails(
            user.getId(),
            user.getFirstname(),
            user.getLastname(),
            user.getEmail(),
            user.getPhoneNumber(),
            user.getPassword(),
            authorities
            ); 
    }






    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
       return email;
    }

    public boolean isAccountNonExpired(){
        return UserDetails.super.isAccountNonExpired();
    }
    
    public boolean isAccountNonLocked(){
        return UserDetails.super.isAccountNonLocked();
    }

    public boolean isCredentialNonExpired(){
        return UserDetails.super.isCredentialsNonExpired();
    }

    public boolean isEnabled(){
        return UserDetails.super.isEnabled();
    }

}
