package com.shops.microservices.msvc_shops.request;

import com.shops.microservices.msvc_shops.entities.Shop;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
@Data
public class ShopSearchRequest {
private String city;
private Shop.ShopType type;

@Min(value = 0, message = "Page must be 0' or greater")
private Integer page = 0;

@Min(value = 1, message = "Size must be at least 1")
@Max(value = 100, message = "Size must no exceed 100")
private Integer size = 20;

    private String sortBy = "name";
    private String sortDirection = "asc";
}
