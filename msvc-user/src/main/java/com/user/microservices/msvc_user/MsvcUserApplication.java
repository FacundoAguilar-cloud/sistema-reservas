package com.user.microservices.msvc_user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsvcUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcUserApplication.class, args);
	}

}
