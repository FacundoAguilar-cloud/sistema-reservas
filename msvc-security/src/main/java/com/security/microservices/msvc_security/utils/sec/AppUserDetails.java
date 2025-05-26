package com.security.microservices.msvc_security.utils.sec;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AppUserDetails implements UserDetails {

    //public static AppUserDetails createUserDetails() esto lo vamos a poder completar una vez tengamos la conexion lista con el cliente del usuario mediante feign
    
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAuthorities'");
    }

    @Override
    public String getPassword() {
        return null; //esto ahora lo arreglamos

    }

    @Override
    public String getUsername() {
        return null; //idem
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
