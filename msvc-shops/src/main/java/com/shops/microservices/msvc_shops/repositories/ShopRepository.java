package com.shops.microservices.msvc_shops.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.shops.microservices.msvc_shops.entities.Shop;

@Repository
public interface ShopRepository extends CrudRepository<Shop, Long> {

List <Shop> findByOwner(Long ownerId);

List <Shop> findByCity(String city);

List <Shop> findByShopType(Shop.ShopType shopType);

//con esto chequeamos si un negocio existe tanto por su nombre como por su propietarioo
boolean existsByNameAndOwnerUserId(String name, Long ownerUserId); //tendria que ignorar el case


}
