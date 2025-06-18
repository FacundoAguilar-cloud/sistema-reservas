package com.shops.microservices.msvc_shops.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.shops.microservices.msvc_shops.entities.Shop;

@Repository
public interface ShopRepository extends CrudRepository<Shop, Long> {

}
