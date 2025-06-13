package com.user.microservices.msvc_user.test;

import java.util.Set;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.user.microservices.msvc_user.entities.Role;
import com.user.microservices.msvc_user.entities.User;
import com.user.microservices.msvc_user.repositories.UserRepository;


import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DataInitializer implements ApplicationListener <ApplicationReadyEvent> {
private final UserRepository userRepository;
private final PasswordEncoder passwordEncoder;
private final EntityManager entityManager;

@Override //aca creamos los roles
public void onApplicationEvent(ApplicationReadyEvent event) {
    Set <String> defaultRoles = Set.of("ROLE_CLIENT", "ROLE_ADMIN");
    createDefaultUserIfNotExist();
    createDefaultAdminIfNotExist();
}

private void createDefaultUserIfNotExist(){
    Role userRole = Role.CLIENT;

for (int i = 0; i <= 5; i++) {
    String defaultEmail = "user" + i + "@email.com";
    if (userRepository.findByEmail(defaultEmail).isPresent()) continue;
    
    User user = new User();
    user.setFirstname("User");
    user.setLastname("User " + i);
    user.setEmail(defaultEmail);
    user.setPhoneNumber("1122345678");
    user.setPassword(passwordEncoder.encode("123456asd"));

    user.setRoles(Set.of(userRole));
    userRepository.save(user);

}                
}

private void createDefaultAdminIfNotExist(){
    Role adminRole = Role.ADMIN;
    for(int i = 1; i <= 2; i++ ){
        String defaultEmail = "admin" +i+"@email.com";
        if (userRepository.findByEmail(defaultEmail).isPresent()) {
            continue;
    }
    User user = new User();
    user.setFirstname("Admin");
    user.setLastname("Admin" + i);
    user.setEmail(defaultEmail);
    user.setPhoneNumber("112245678");
    user.setPassword(passwordEncoder.encode("1234asd"));
    user.setRoles(Set.of(adminRole));
    userRepository.save(user);

}
}







}
