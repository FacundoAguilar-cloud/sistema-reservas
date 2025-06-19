package com.shops.microservices.msvc_shops.services;

import java.math.BigDecimal;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shops.microservices.msvc_shops.config.ShopMapper;
import com.shops.microservices.msvc_shops.entities.Shop;
import com.shops.microservices.msvc_shops.entities.Shop.ShopType;
import com.shops.microservices.msvc_shops.exceptions.ResourceNotFoundException;
import com.shops.microservices.msvc_shops.reponse.ShopResponse;
import com.shops.microservices.msvc_shops.repositories.ShopRepository;
import com.shops.microservices.msvc_shops.request.ShopCreateRequest;
import com.shops.microservices.msvc_shops.request.ShopUpdateRequest;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByOwner'");
    }

    @Override
    public Page<ShopResponse> findByCity(String city) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByCity'");
    }

    @Override
    public Page<ShopResponse> findByType(ShopType type, Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByType'");
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
    public ShopResponse updateShop(ShopUpdateRequest request, Long ownerId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateShop'");
    }

    @Override
    public void deleteShop(Long shopId, Long ownerId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteShop'");
    }

    @Override
    public List<ShopResponse> findNearbyShops(BigDecimal latitude, BigDecimal longitude, Double radiusKm) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findNearbyShops'");
    }

}
