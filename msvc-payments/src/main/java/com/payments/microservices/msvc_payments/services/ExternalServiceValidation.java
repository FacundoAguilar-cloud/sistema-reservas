package com.payments.microservices.msvc_payments.services;

import org.springframework.stereotype.Service;

import com.payments.microservices.msvc_payments.client.AppointmentClient;
import com.payments.microservices.msvc_payments.client.ShopClient;
import com.payments.microservices.msvc_payments.client.UserClient;
import com.payments.microservices.msvc_payments.dto.AppointmentDto;
import com.payments.microservices.msvc_payments.dto.ShopDto;
import com.payments.microservices.msvc_payments.dto.UserDto;
import com.payments.microservices.msvc_payments.request.PaymentCreateRequest;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalServiceValidation {
//ACA SE VAN A VALIDAR SERVICIOS EXTERNOS (SHOP, USER, APPOINTMENT, ETC)
private final UserClient userClient;
private final ShopClient shopClient;
private final AppointmentClient appointmentClient;

public ValidationContext validateExternalEntities(PaymentCreateRequest request){
    UserDto userDto = validateUser(request.getUserId());
    ShopDto shopDto = validateShop(request.getShopId());
    AppointmentDto appointmentDto = validateAppointment(request.getAppointmentId());

    return ValidationContext.builder()
    .user(userDto)
    .shop(shopDto)
    .appointment(appointmentDto)
    .build();
}

private UserDto validateUser(Long userId){
try {
    UserDto user = userClient.getUserById(userId);
    if (user == null) {
        throw new IllegalArgumentException("User not found.");
    }
    return user;
} catch (FeignException.NotFound e) {
    throw new IllegalArgumentException("User not found.");
}
catch(FeignException e){
    throw new RuntimeException("User service not available, please try again.");
}
}

private ShopDto validateShop(Long shopId){
    try {
        ShopDto shop = (ShopDto) shopClient.getShopById(shopId);
        if (shop == null) {
            throw new IllegalArgumentException("Shop not found.");
        }
        return shop;
    } catch (FeignException.NotFound e) {
        throw new IllegalArgumentException("Shop not found.");
    }
    catch(FeignException e){
        throw new RuntimeException("Shop service not available, please try again");
    }
}

private AppointmentDto validateAppointment(Long appointmentId){
    try {
        AppointmentDto appointment = appointmentClient.getAppointmentById(appointmentId);
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment not found.");
        }
        return appointment;
    } catch (FeignException.NotFound e) {
        throw new IllegalArgumentException("Appoinbtment not found");
    }
    catch(FeignException e){
        throw new RuntimeException("Appointment service not available, please try again");
    }
}


}
