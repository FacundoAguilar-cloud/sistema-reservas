package com.appointments.microservices.msvc_appoinments.config;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.appointments.microservices.msvc_appoinments.dto.ShopDto;

@Component
public class ShopMapper {
 public ShopDto mapToShopDto(Map<String, Object> shopData) {
        if (shopData == null) {
            return null;
        }
        
        ShopDto shopDto = new ShopDto();
        
        // Mapeo seguro con validación de tipos
        shopDto.setId(extractLong(shopData, "id"));
        shopDto.setOwnerId(extractLong(shopData, "ownerId"));
        shopDto.setName(extractString(shopData, "name"));
        shopDto.setAddress(extractString(shopData, "address"));
        shopDto.setPhone(extractString(shopData, "phone"));
        shopDto.setOpeningTime(parseLocalTime(extractString(shopData, "openingTime")));
        shopDto.setClosingTime(parseLocalTime(extractString(shopData, "closingTime")));
        shopDto.setStatus(extractString(shopData, "status"));
        
        return shopDto;
    }
    
    private Long extractLong(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    private String extractString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : null;
    }

    private java.time.LocalTime parseLocalTime(String timeString) {
        if (timeString == null || timeString.isEmpty()) {
            return null;
        }
        try {
            return java.time.LocalTime.parse(timeString);
        } catch (Exception e) {
            return null;
        }
    }
}

