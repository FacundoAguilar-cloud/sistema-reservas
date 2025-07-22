package com.appointments.microservices.msvc_appoinments.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@FeignClient(name = "msvc-shops")
public interface ShopClient { 
@GetMapping("/api/shop/get-by-id/{shopId}")
    Map<String, Object> getShopById(@PathVariable("shopId") Long id);
}
