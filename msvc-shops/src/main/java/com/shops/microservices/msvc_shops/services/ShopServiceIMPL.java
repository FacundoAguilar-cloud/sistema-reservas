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

Page <ShopResponse>  findAll(Pageable pageable);

ShopResponse findById(Long shopId); //ok

List <ShopResponse> findByOwner(Long ownerId); //ok

Page<ShopResponse> findByCity(String city, Pageable pageable); //ok

Page<ShopResponse> findByType(Shop.ShopType type, Pageable pageable); //ok

ShopResponse createShop(ShopCreateRequest request, Long ownerId); //ok

ShopResponse updateShop(Long shopId,ShopUpdateRequest request, Long ownerId); //ok

void deleteShop(Long shopId, Long ownerId); //ok

List <ShopResponse> findNearbyShops(BigDecimal latitude, BigDecimal longitude, Double radiusKm); //este metodo nos va a requerir algun que otro trabajo extra en la entidad, revisar



}
