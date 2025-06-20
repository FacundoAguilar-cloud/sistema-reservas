package com.shops.microservices.msvc_shops.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shops.microservices.msvc_shops.config.ShopMapper;
import com.shops.microservices.msvc_shops.entities.Shop;
import com.shops.microservices.msvc_shops.exceptions.ResourceNotFoundException;
import com.shops.microservices.msvc_shops.exceptions.UnauthorizedException;
import com.shops.microservices.msvc_shops.reponse.ShopResponse;
import com.shops.microservices.msvc_shops.repositories.ShopRepository;
import com.shops.microservices.msvc_shops.request.ShopCreateRequest;
import com.shops.microservices.msvc_shops.request.ShopUpdateRequest;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional //si ocurre un error todos los cambios se deshacen automaticamente

public class ShopService implements ShopServiceIMPL {
private final ShopRepository shopRepository;
private final ModelMapper modelMapper;
private final ShopMapper shopMapper;    
    
    @Override
     public ShopResponse findById(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
        .orElseThrow(() -> new ResourceNotFoundException("Shop not found, please try again")); //aca vamos a tener que mapear para que funcione

        return modelMapper.map(shop, ShopResponse.class);
    }

    @Override
    public List<ShopResponse> findByOwner(Long ownerId) {
        List<Shop> shops = shopRepository.findByOwner(ownerId);
        if (shops == null || shops.isEmpty()) {
            throw new ResourceNotFoundException("Not found, please try again");
        }
        return shops.stream()
            .map(shopMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public Page<ShopResponse> findByCity(String city, Pageable pageable) {
        Page <Shop> shops = shopRepository.findByCity(city, pageable);
        if (shops == null || shops.isEmpty()) {
            throw new ResourceNotFoundException("Not found, please try again");
        }
        return shops.map(shopMapper::toResponse);
    }

    @Override
    public Page<ShopResponse> findByType(Shop.ShopType type, Pageable pageable) {
        Page <Shop> shops = shopRepository.findByShopType(type, pageable);
        return shops.map(shopMapper::toResponse);
    }

    @Override
    public ShopResponse createShop(ShopCreateRequest request, Long ownerId) {
        //aca deberiamos crear algo para validar que no existe un negocio con el mismo nombre para un propietario en concreto

        if (shopRepository.existsByNameAndOwnerUserId(request.getName(), ownerId)) {
            throw new ValidationException("you already have an establishment with that name");
        }
        Shop shop = shopMapper.toEntity(request);
        shop.setOwnerId(ownerId);
        Shop savedShop = shopRepository.save(shop);
        
        return shopMapper.toResponse(savedShop);
    }

    @Override
    public ShopResponse updateShop(Long shopId,ShopUpdateRequest request, Long ownerId) {
        Shop shop = shopRepository.findById(shopId)
            .orElseThrow(() -> new ResourceNotFoundException("Shop not found, please try again"));

        validateOwnership(shop, ownerId);

        shopMapper.updateEntity(shop, request);

        Shop updatedShop = shopRepository.save(shop);

        return shopMapper.toResponse(updatedShop);
    
    }

    @Override
    public void deleteShop(Long shopId, Long ownerId) {
        Shop shop = shopRepository.findById(shopId)
            .orElseThrow(() -> new ResourceNotFoundException("Shop not found, please try again"));
        validateOwnership(shop, ownerId);

        shopRepository.delete(shop);
    }

    @Override
    public List<ShopResponse> findNearbyShops(BigDecimal latitude, BigDecimal longitude, Double radiusKm) {
        return null;
        //esto todavia no lo implementamos
    }

      private void validateOwnership(Shop shop, Long userId) {
        if (!shop.getOwnerId().equals(userId)) {
            throw new UnauthorizedException("No tienes permisos para modificar este establecimiento");
        }

}

}
