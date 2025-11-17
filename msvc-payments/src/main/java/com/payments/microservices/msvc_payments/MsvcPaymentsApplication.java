package com.payments.microservices.msvc_payments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = "com.payments.microservices.msvc_payments") //ver esto mas tarde, supuestamente esta ok que lo agregue
@EnableFeignClients
public class MsvcPaymentsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcPaymentsApplication.class, args);
	}

}
