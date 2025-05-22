package com.security.microservices.msvc_security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsvcSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcSecurityApplication.class, args);
	}

}
