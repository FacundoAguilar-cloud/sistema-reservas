package com.security.microservices.msvc_security.test;

import java.util.Set;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.security.microservices.msvc_security.client.UserClient;
import com.security.microservices.msvc_security.commons.newUserRequest;
import com.security.microservices.msvc_security.entities.Role;


import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//esta clase la vamos a crear para hacer pruebas, apenas se inicie la app como tal se va a crear un admin por defecto
@Component
@Slf4j
@RequiredArgsConstructor
public class AdminInit implements ApplicationRunner{
private final UserClient userClient;
private final PasswordEncoder passwordEncoder;

  @Override
    public void run(ApplicationArguments args) {
        String defaultAdminEmail = "admin1@barberapp.com";
        int retries = 10;
        int waitMs = 2000;
        
        log.info("Iniciando verificación de usuario admin...");
        
        for (int i = 0; i < retries; i++) {
            try {
                // Intentar buscar el usuario existente
                var existing = userClient.findByEmail(defaultAdminEmail);
                if (existing != null) {
                    log.info("Usuario admin ya existe, saltando creación");
                    return;
                }
                
                // el usuario existe pero es null (practicamente improbable pero remotamente posible)
                break;
                
            } catch (FeignException.NotFound e) {
                // Como el usuario no existe, sale del bucle para crearlo
                log.info("Usuario admin no existe, procediendo a crear...");
                break;
                
            } catch (FeignException.ServiceUnavailable | FeignException.InternalServerError e) {
                // Servicio no disponible, reintentar
                log.warn("Intento {}/{}: Servicio msvc-user no disponible, reintentando en {}ms...", 
                         i + 1, retries, waitMs);
                
                if (i < retries - 1) {
                    try {
                        Thread.sleep(waitMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Interrupción durante espera, abortando inicialización");
                        return;
                    }
                } else {
                    log.error("No se pudo conectar al servicio msvc-user después de {} intentos", retries);
                    return;
                }
                
            } catch (Exception e) {
                log.error("Error inesperado al verificar usuario admin: {}", e.getMessage());
                return;
            }
        }
        
        // Crear el usuario admin
        createDefaultAdmin(defaultAdminEmail);
    }
    
    private void createDefaultAdmin(String email) {
        try {
            newUserRequest admin = new newUserRequest();
            admin.setFirstname("Facundo");
            admin.setLastname("Luzny");
            admin.setEmail(email);
            admin.setPhoneNumber("1122345631");
            admin.setPassword(passwordEncoder.encode("1234asd"));
            admin.setRoles(Set.of(Role.ROLE_ADMIN.name()));

            userClient.createNewUser(admin);
            log.info("Usuario admin creado exitosamente");
            
        } catch (Exception e) {
            log.error("Error al crear usuario admin: {}", e.getMessage());
        }
    }
}
