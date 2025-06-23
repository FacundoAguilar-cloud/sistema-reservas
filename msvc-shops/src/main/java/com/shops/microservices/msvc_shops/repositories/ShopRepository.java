package com.shops.microservices.msvc_shops.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.shops.microservices.msvc_shops.entities.Shop;

@Repository
public interface ShopRepository extends CrudRepository<Shop, Long> {

Page <Shop> findAll(Pageable pageable);    //nos da TODAS las tiendas en un page

List <Shop> findByOwnerId(Long ownerId);

Page <Shop> findByCity(String city, Pageable pageable);

Page <Shop> findByShopType(Shop.ShopType shopType, Pageable pageable);

//con esto chequeamos si un negocio existe tanto por su nombre como por su propietario
boolean existsByNameAndOwnerId(String name, Long ownerId); //tendria que ignorar el case


}
