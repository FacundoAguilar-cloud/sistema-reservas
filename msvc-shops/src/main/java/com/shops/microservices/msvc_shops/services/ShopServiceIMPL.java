package com.shops.microservices.msvc_shops.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.shops.microservices.msvc_shops.entities.Shop;
import com.shops.microservices.msvc_shops.reponse.ShopResponse;
import com.shops.microservices.msvc_shops.request.ShopCreateRequest;
import com.shops.microservices.msvc_shops.request.ShopUpdateRequest;

public interface ShopServiceIMPL {


ShopResponse findById(Long shopId);

List <ShopResponse> findByOwner(Long ownerId);

Page <ShopResponse> findByCity(String city);

Page<ShopResponse> findByType(Shop.ShopType type, Pageable pageable);

ShopResponse createShop(ShopCreateRequest request, Long ownerId);

ShopResponse updateShop(ShopUpdateRequest request, Long ownerId);

void deleteShop(Long shopId, Long ownerId);

List <ShopResponse> findNearbyShops(BigDecimal latitude, BigDecimal longitude, Double radiusKm); //este metodo nos va a requerir algun que otro trabajo extra en la entidad, revisar



}
