package com.appointments.microservices.msvc_appoinments.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.appointments.microservices.msvc_appoinments.dto.ShopDto;

@FeignClient(name = "msvc-shops")
public interface ShopClient { 
@GetMapping("/api/shop/get-by-id/{shopId}")
    ShopDto getShopById(@PathVariable("shopId") Long id);
}
