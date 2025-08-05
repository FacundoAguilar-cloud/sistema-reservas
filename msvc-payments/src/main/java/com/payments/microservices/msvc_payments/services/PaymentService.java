package com.payments.microservices.msvc_payments.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


import org.springframework.stereotype.Service;

import com.payments.microservices.msvc_payments.client.AppointmentClient;
import com.payments.microservices.msvc_payments.client.ShopClient;
import com.payments.microservices.msvc_payments.client.UserClient;
import com.payments.microservices.msvc_payments.config.PaymentMapper;
import com.payments.microservices.msvc_payments.dto.AppointmentDto;
import com.payments.microservices.msvc_payments.dto.AppointmentStatus;
import com.payments.microservices.msvc_payments.dto.ShopDto;
import com.payments.microservices.msvc_payments.dto.UserDto;
import com.payments.microservices.msvc_payments.entities.Payment;
import com.payments.microservices.msvc_payments.exceptions.ResourceNotFoundException;
import com.payments.microservices.msvc_payments.repositories.PaymentRepository;
import com.payments.microservices.msvc_payments.request.PaymentCreateRequest;
import com.payments.microservices.msvc_payments.request.PaymentInfoUpdateRequest;
import com.payments.microservices.msvc_payments.request.PaymentStatusUpdateRequest;
import com.payments.microservices.msvc_payments.response.PaymentResponse;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j

public class PaymentService implements PaymentServiceIMPL {
private final AppointmentClient appointmentClient;
private final UserClient userClient;
private final ShopClient shopClient;
private final PaymentRepository paymentRepository;
private final PaymentMapper paymentMapper;
@Override
public PaymentResponse createPayment(PaymentCreateRequest request) {
   //validamos si el usuario existe:
try {
    UserDto user = userClient.getUserById(request.getUserId());
    if (user == null) {
        throw new IllegalArgumentException("User not found.");
    }
} catch (FeignException.NotFound e) {
   throw new IllegalArgumentException("User not found.");
}
catch (FeignException e){
throw new RuntimeException("User service not available, try again later.");
}

//validamos que la tienda exista
try {
    ShopDto shop = (ShopDto) shopClient.getShopById(request.getShopId());
    if (shop == null) {
        throw new IllegalArgumentException("Shop not found");
    }
} catch (FeignException.NotFound e) {
    throw new IllegalArgumentException("Shop not found")
} catch(FeignException e){
 throw new RuntimeException("Shop service not available, try again later.");
}

//validar que la cita existe y puede ser pagada
AppointmentDto appointment;
try {
    appointment = appointmentClient.getAppointmentById(request.getAppointmentId());
    if (appointment == null) {
       throw new IllegalArgumentException("Appointment not found."); 
    }
} catch (FeignException.NotFound e) {
    throw new IllegalArgumentException("Appointment not found");
} 
catch(FeignException e){
   throw new IllegalArgumentException("Appointment service not available, try again later.");
}

//validaciones para la lógica del genocio 


validateIfUserCanPayAppointment(request.getUserId(), appointment); //OK

validateIfShopMatch(request.getShopId(), request.getShopId());    //OK

validateIfPaymentDoesNotExist(request.getAppointmentId());        //OK

validatePaymentAmount(request.getAmount());

validatePaymentMethod(request);


Payment payment = paymentMapper.toEntity(request, appointment);

Payment savedPayment = paymentRepository.save(payment);

return paymentMapper.toResponseDto(savedPayment);
}
@Override
public PaymentResponse getPaymentById(Long paymentId) {
  Payment payment = paymentRepository.findById(paymentId)
  .orElseThrow(() -> new ResourceNotFoundException("Payment not found."));
  //vamos a tener que hacer un DTO para convertir el PR a ResponseDTO
}
@Override
public PaymentResponse updatePayment(PaymentInfoUpdateRequest request, Long paymentId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'updatePayment'");
}
@Override
public void deletePayment(Long paymentId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deletePayment'");
}
@Override
public List<PaymentResponse> getPaymentsByUserId(Long userId) {
    List <Payment> payments = paymentRepository.findPaymentByUserId(userId); 
    return paymentMapper.toResponseDtoList(payments);
    
}
@Override
public List<PaymentResponse> getPaymentsByShopId(Long shopId) {
    List <Payment> payments = paymentRepository.findPaymentByShopId(shopId);
    return paymentMapper.toResponseDtoList(payments);
}
@Override
public List<PaymentResponse> getPaymentsByAppointmentId(Long paymentId) {
   List <Payment> payments = paymentRepository.findPaymentByAppointmentId(paymentId);
   return paymentMapper.toResponseDtoList(payments);
}
@Override
public PaymentResponse processPayment(Long paymentId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'processPayment'");
}
@Override
public PaymentResponse updatePaymentStatus(PaymentStatusUpdateRequest request, Long paymentId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'updatePaymentStatus'");
}
@Override
public PaymentResponse confirmPayment(String transactionId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'confirmPayment'");
}
@Override
public boolean canAppointmentBePaid(Long appointmentId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'canAppointmentBePaid'");
}
@Override
public boolean paymentExistsForAppointment(Long appointmentId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'paymentExistsForAppointment'");
}
//validaciones para logica de negocio
private void validateIfUserCanPayAppointment(Long userId, AppointmentDto appointmentDto){
    if (!appointmentDto.getClientId().equals(userId)) {
        throw new SecurityException("User is not authorized to pay for the appointment");
    }
 }

 private void validateIfAppointmentCanBePaid(AppointmentDto appointment){
    if (appointment.getStatus() != AppointmentStatus.PENDING &&
    appointment.getStatus() != AppointmentStatus.CONFIRMED) {
        throw new IllegalStateException("Appointment cannot be paid, check the status and wait.");
    }
    if (appointment.getCancelledAt() != null) {
        throw new IllegalStateException("Cannot pay for cancelled appointments");
    }

    if (appointment.getAppointmentDate().isBefore(LocalDate.now())) {
        throw new IllegalStateException("Cannot pay for past appointments");
    }
 }
  

private void validateIfShopMatch(Long shopId, Long appointmentShopId) {
    if (!shopId.equals(appointmentShopId)) {
        throw new IllegalArgumentException("Shop ID does not match the appointment's shop.");
    }
}

private void validateIfPaymentDoesNotExist(Long appointmentId){
    if (!paymentRepository.existsByAppointmentId(appointmentId)) {
        throw new IllegalStateException("Payment already exist for this appointment.");
    }
}

private void validatePaymentAmount(BigDecimal requestAmount){
    if (requestAmount.compareTo(servicePrice) != 0) {
        throw new IllegalStateException("Payment amount does not match the expected servie price.");
        
    }
}

private void validatePaymentMethod(PaymentCreateRequest request){

}


 
}
