package com.shops.microservices.msvc_shops.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shops.microservices.msvc_shops.entities.Shop;
import com.shops.microservices.msvc_shops.reponse.ShopResponse;
import com.shops.microservices.msvc_shops.request.ShopCreateRequest;
import com.shops.microservices.msvc_shops.request.ShopSearchRequest;
import com.shops.microservices.msvc_shops.request.ShopUpdateRequest;
import com.shops.microservices.msvc_shops.services.ShopServiceIMPL;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;








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
public ResponseEntity<Page<ShopResponse>> shopSearch(@Valid @ModelAttribute ShopSearchRequest request) { 
  Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
  Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
   //esto seria para una ciudad específica
  if (request.getCity() != null && !request.getCity().trim().isEmpty()) {
   Page <ShopResponse> shops = shopServiceIMPL.findByCity(request.getCity(), pageable);
   return ResponseEntity.ok(shops);
  }
  if (request.getType() != null && (request.getCity() == null || request.getCity().trim().isEmpty())) {
   Page <ShopResponse> shops = shopServiceIMPL.findByType(request.getType(), pageable);
   return ResponseEntity.ok(shops);
  }
  Page<ShopResponse> shops = shopServiceIMPL.findAll(pageable);
  return ResponseEntity.ok(shops);
}

@GetMapping("/all-types") 
public ResponseEntity<Shop.ShopType[]> getAllTypes() {
return ResponseEntity.ok(Shop.ShopType.values());
}


@PostMapping("/create")
@PreAuthorize("hasRole('ROLE_SHOP_OWNER')")
//aca faltaria todo el apartado de seguridad, eso se va a hacer mas adelante, aca solo estamos terminando la estructura del controlador (usariamos requestHeader)
public ResponseEntity<ShopResponse> createShop(@Valid @RequestBody ShopCreateRequest request, Long userId) {
      ShopResponse shopResponse = shopServiceIMPL.createShop(request, userId);
      return ResponseEntity.status(HttpStatus.CREATED).body(shopResponse);
   
}

@PutMapping("/update/{shopId}/{ownerId}")
@PreAuthorize("hasRole('ROLE_SHOP_OWNER')")
public ResponseEntity<ShopResponse> updateShop(@PathVariable Long shopId,@PathVariable Long ownerId , @Valid @RequestBody ShopUpdateRequest request) {
      ShopResponse shopResponse = shopServiceIMPL.updateShop(ownerId, request, shopId);
      return ResponseEntity.status(HttpStatus.OK).body(shopResponse);
}


@DeleteMapping("/delete/{ownerId}/{shopId}") //el ownerId deberia de venir inyectado
@PreAuthorize("hasRole('ROLE_SHOP_OWNER')")
public ResponseEntity<Void> deleteShop(@PathVariable Long ownerId, @PathVariable Long shopId) {
      shopServiceIMPL.deleteShop(shopId, ownerId);
      return ResponseEntity.noContent().build();

}

//AGREGAR LAS EXCEPCIONES AL HANDLER PARA NO USAR TRY-CATCH Y ALARGAR EL CÓDIGO {IMPORTANTE}





}