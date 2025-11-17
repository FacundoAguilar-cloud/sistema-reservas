package com.payments.microservices.msvc_payments.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.mercadopago.MercadoPagoConfig;

@Configuration
public class MercadoPagoConfiguration {
    @Value("${mercadopago.access-token}")
    private String accessToken;
    
    
    
    @Bean
    @Primary
    public MercadoPagoConfig mercadoPagoConfig(){
        MercadoPagoConfig.setAccessToken(accessToken);
        return new MercadoPagoConfig();
    }

    

}
