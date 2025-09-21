package com.payments.microservices.msvc_payments.services;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.payments.microservices.msvc_payments.client.AppointmentClient;
import com.payments.microservices.msvc_payments.client.ShopClient;
import com.payments.microservices.msvc_payments.dto.AppointmentDto;
import com.payments.microservices.msvc_payments.entities.Payment;
import com.payments.microservices.msvc_payments.exceptions.PaymentException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentAuthorizationService {

private final ShopClient shopClient;
private final AppointmentClient appointmentClient;

public void validateUserPermissions(Payment payment, Long userId){
    log.debug("Validation user permissions for payment.", userId, payment.getId());


    if (payment.getUserId().equals(userId)) {
        log.debug("User is payment owner.");
        return;
    }

    if (isShopOwner(payment.getShopId(), userId)) {
        log.debug("User is shop owner.");
        return;
    }

    if (isProfessionalOfAppointment(payment.getAppointmentId(), userId)) {
        log.debug("User is appointment professional.");
        return;
    }

    throw new PaymentException("You don´t have permissions to access this payment.");
        
    }

private boolean isShopOwner(Long shopId, Long userId) {
   try {
     Map <String, Object> shopData = shopClient.getShopById(shopId);
     if (shopData != null) {
        Long ownerId = (Long) shopData.get("ownerId");
        return ownerId != null && ownerId.equals(userId);
     }
   } catch (Exception e) {
    log.warn("Error validating shop owner for shop." , shopId, e);
   }
   return false;
}

private boolean isProfessionalOfAppointment(Long appointmentId, Long userId) {
   try {
     AppointmentDto appointment = appointmentClient.getAppointmentById(appointmentId);
     if (appointment != null) {
        return appointment.getBarberId().equals(userId);
     }
   } catch (Exception e) {
   log.warn("Error validating professional for appointment.");
   }
   return false;
}


}







