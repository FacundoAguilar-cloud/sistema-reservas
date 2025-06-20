package com.shops.microservices.msvc_shops.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.shops.microservices.msvc_shops.reponse.ShopResponse;
import com.shops.microservices.msvc_shops.services.ShopServiceIMPL;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/shop")
public class ShopController {
private final ShopServiceIMPL shopServiceIMPL;   

@GetMapping("/get-by-id/{shopId}")
public ResponseEntity<ShopResponse> getShopById(@PathVariable Long shopId) {
   ShopResponse shop = shopServiceIMPL.findById(shopId);
   
   return ResponseEntity.ok(shop);


}
@GetMapping("/get-by-owner")
public ResponseEntity<ShopResponse> getByOwner(@PathVariable Long shopId) {
   ShopResponse shop = shopServiceIMPL.findById(shopId);
   
   return ResponseEntity.ok(shop);


}

@GetMapping("/search")
public ResponseEntity<Page<ShopResponse>> shopSearch(@RequestParam ShopSearchRequest request) { //este DTO todavia no lo hicimos, HACER!
   return null;
}




}