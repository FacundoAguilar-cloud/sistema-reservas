package com.appointments.microservices.msvc_appoinments.client;

import org.springframework.stereotype.Component;

import com.appointments.microservices.msvc_appoinments.dto.ShopDto;
//tanto esta clase como la de los usuarios es para manejar errores y nada mas, completamente accesorio
@Component
public class ShopClientFallback implements ShopClient {

    @Override
    public ShopDto getShopById(Long id) {
       return null;
    }

}
