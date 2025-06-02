package com.security.microservices.msvc_security.test;

import java.util.Set;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.security.microservices.msvc_security.client.UserClient;
import com.security.microservices.msvc_security.commons.newUserRequest;
import com.security.microservices.msvc_security.entities.RoleName;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

//esta clase la vamos a crear para hacer pruebas, apenas se inicie la app como tal se va a crear un admin por defecto
@Component
@RequiredArgsConstructor
public class AdminInit implements ApplicationRunner{
private final UserClient userClient;
private final PasswordEncoder passwordEncoder;

 @Override
    public void run(ApplicationArguments args) {
        String defaultAdminEmail = "admin1@barberapp.com";

        try {
            var existing = userClient.findByEmail(defaultAdminEmail);
            if (existing != null) {
                System.out.println("Admin already exists");
                return;
            }
        } catch (FeignException.NotFound e) {
            // no existe
        }

        newUserRequest admin = new newUserRequest();
        admin.setFirstname("Facundo");
        admin.setLastname("Luzny");
        admin.setEmail(defaultAdminEmail);
        admin.setPhoneNumber("1122345631");
        admin.setPassword(passwordEncoder.encode("1234asd")); 
        admin.setRoles(Set.of(RoleName.ROLE_ADMIN));

        try {
            userClient.createNewUser(admin);
            System.out.println("Default admin user created");
        } catch (Exception e) {
            System.err.println("Failed to create default admin user: " + e.getMessage());
        }
    }
}

