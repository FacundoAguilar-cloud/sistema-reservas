package com.user.microservices.msvc_user.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.user.microservices.msvc_user.exceptions.ResourceNotFoundException;
import com.user.microservices.msvc_user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.user.microservices.msvc_user.entities.User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found, please try again"));

        return UserAppDetails.createUserDetails(user);
    }

}
