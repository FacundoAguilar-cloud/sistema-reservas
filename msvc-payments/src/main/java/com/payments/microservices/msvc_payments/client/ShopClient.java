package com.payments.microservices.msvc_payments.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "msvc-shops") //esto obviamente lo vamos a necestiar mas adelante, así que ya lo dejamos listo
public interface ShopClient { 
    @GetMapping("/api/shop/get-by-id/{shopId}")
    Map<String, Object> getShopById(@PathVariable("shopId") Long id);
}
