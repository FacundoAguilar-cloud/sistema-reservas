package com.shops.microservices.msvc_shops.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shops.microservices.msvc_shops.entities.Shop;
import com.shops.microservices.msvc_shops.reponse.ShopResponse;
import com.shops.microservices.msvc_shops.request.ShopCreateRequest;
import com.shops.microservices.msvc_shops.request.ShopSearchRequest;
import com.shops.microservices.msvc_shops.request.ShopUpdateRequest;
import com.shops.microservices.msvc_shops.security.JwtValidator;
import com.shops.microservices.msvc_shops.services.ShopServiceIMPL;

import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PutMapping;








@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/api/shop")
public class ShopController {
private final ShopServiceIMPL shopServiceIMPL;
private final JwtValidator jwtValidator;  

@GetMapping("/get-by-id/{shopId}")
public ResponseEntity<ShopResponse> getShopById(@PathVariable Long shopId) {
   ShopResponse shop = shopServiceIMPL.findById(shopId);
   
   return ResponseEntity.ok(shop);
}
@GetMapping("/get-by-owner")
public ResponseEntity<ShopResponse> getByOwner(@PathVariable Long ownerId) {
   ShopResponse shop = shopServiceIMPL.findById(ownerId);
   
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
  if (request.getShopType() != null && (request.getCity() == null || request.getCity().trim().isEmpty())) {
   Page <ShopResponse> shops = shopServiceIMPL.findByType(request.getShopType(), pageable);
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
@PreAuthorize("hasAuthority('SHOP_OWNER')")
//implementacion limpia con - código
public ResponseEntity<ShopResponse> createShop(
      @Valid 
      @RequestBody ShopCreateRequest request,
      @RequestHeader("Authorization") String authHeader,
      Authentication authentication,
      HttpServletRequest httpRequest) {
     Long ownerId = extractOwnerIdFromRequest(httpRequest);

     if (ownerId == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
     }
     
    ShopResponse shopResponse = shopServiceIMPL.createShop(request, ownerId);
    return ResponseEntity.status(HttpStatus.CREATED).body(shopResponse);    
   
       }

//recordar que no utilizamos mas el ownerId, sino el userId 
@PutMapping("/update/{shopId}/{userId}")
@PreAuthorize("hasRole('ROLE_SHOP_OWNER')")
public ResponseEntity<ShopResponse> updateShop(
      @PathVariable Long shopId, 
      @Valid @RequestBody ShopUpdateRequest request,
       @RequestHeader ("Authorization") String authHeader) {

       String token = authHeader.replace("Bearer", "");
       Long userId = jwtValidator.getIdFromToken(token);
            
      ShopResponse shopResponse = shopServiceIMPL.updateShop(userId, request, shopId);
      return ResponseEntity.status(HttpStatus.OK).body(shopResponse);
}


@DeleteMapping("/delete/{shopId}") //el ownerId deberia de venir inyectado
@PreAuthorize("hasRole('ROLE_SHOP_OWNER')")
public ResponseEntity<Void> deleteShop(
      @PathVariable Long shopId,
      @RequestHeader ("Authorization") String authHeader ) {
      
      String token = authHeader.replace("Bearer", "");
      Long userId = jwtValidator.getIdFromToken(token);
      
      shopServiceIMPL.deleteShop(shopId, userId);
      return ResponseEntity.noContent().build();

}

private Long extractOwnerIdFromRequest(HttpServletRequest request) {
    try {
        // Extraer el token del header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            // Usar tu JwtService para extraer el ID
            return jwtValidator.getIdFromToken(token); // Necesitas este método en JwtService
        }
        return null;
    } catch (Exception e) {
        System.out.println("Error extrayendo owner ID: " + e.getMessage());
        return null;
    }




}


}