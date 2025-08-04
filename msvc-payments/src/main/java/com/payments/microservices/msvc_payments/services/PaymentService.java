package com.payments.microservices.msvc_payments.services;

import java.util.List;


import org.springframework.stereotype.Service;

import com.payments.microservices.msvc_payments.client.AppointmentClient;
import com.payments.microservices.msvc_payments.client.ShopClient;
import com.payments.microservices.msvc_payments.client.UserClient;
import com.payments.microservices.msvc_payments.config.PaymentMapper;
import com.payments.microservices.msvc_payments.entities.Payment;
import com.payments.microservices.msvc_payments.exceptions.ResourceNotFoundException;
import com.payments.microservices.msvc_payments.repositories.PaymentRepository;
import com.payments.microservices.msvc_payments.request.PaymentCreateRequest;
import com.payments.microservices.msvc_payments.request.PaymentInfoUpdateRequest;
import com.payments.microservices.msvc_payments.request.PaymentStatusUpdateRequest;
import com.payments.microservices.msvc_payments.response.PaymentResponse;

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


 
}
