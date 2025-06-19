package com.shops.microservices.msvc_shops.config;

import org.springframework.stereotype.Component;

import com.shops.microservices.msvc_shops.entities.Shop;
import com.shops.microservices.msvc_shops.reponse.ShopResponse;
import com.shops.microservices.msvc_shops.request.ShopCreateRequest;

@Component
public class ShopMapper {

public Shop toEntity(ShopCreateRequest request){
    Shop shop = new Shop();
    shop.setName(request.getName());
    shop.setDescription(request.getDescription());
    shop.getAdress();
    shop.getCity();
    shop.getState();
    shop.getCountry();
    shop.getPhone();
    shop.getEmail();
    shop.setAdvanceBookingDays(request.getAdvanceBookingDays());
    shop.setAdvanceCancellationHours(request.getAdvanceCancellationHours());
    shop.setType(Shop.ShopType.valueOf(request.getType().name()));
    return shop;
}

public ShopResponse toResponse(Shop shop){
     
    ShopResponse response = new ShopResponse();
        response.setId(shop.getId());
        response.setName(shop.getName());
        response.setDescription(shop.getDescription());
        response.setAdress(shop.getAdress());
        response.setCity(shop.getCity());
        response.setState(shop.getState());
        response.setCountry(shop.getCountry());
        response.setPhone(shop.getPhone());
        response.setEmail(shop.getEmail());
        response.setAdvanceBookingDays(shop.getAdvanceBookingDays());
        response.setAdvanceCancellationHours(shop.getAdvanceCancellationHours());
        response.setRating(shop.getRating());
        response.setTotalReviews(shop.getTotalReviews());
        response.setType(ShopResponse.ShopType.valueOf(shop.getType().name()));
        return response;
}


}
