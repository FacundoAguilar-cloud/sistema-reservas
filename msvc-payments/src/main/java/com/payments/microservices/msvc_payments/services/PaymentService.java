package com.payments.microservices.msvc_payments.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.payments.microservices.msvc_payments.client.AppointmentClient;
import com.payments.microservices.msvc_payments.client.ShopClient;
import com.payments.microservices.msvc_payments.client.UserClient;
import com.payments.microservices.msvc_payments.dto.AppointmentDto;
import com.payments.microservices.msvc_payments.entities.Payment;
import com.payments.microservices.msvc_payments.entities.PaymentStatus;
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
    return payments.stream().map(this::convertResponsetoDto)
    .toList();
}
@Override
public List<PaymentResponse> getPaymentsByShopId(Long shopId) {
    List <Payment> payments = paymentRepository.findPaymentByShopId(shopId);
    return payments.stream().map(this::convertResponseToDto)
    .toList();
}
@Override
public List<PaymentResponse> getPaymentsByAppointmentId(Long paymentId) {
   List <Payment> payments = paymentRepository.findPaymentByAppointmentId(paymentId);
   return payments.stream().map(this::convertResponseDto)
   .toList();
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


private Payment createPaymentEntity(PaymentCreateRequest request, AppointmentDto appointmentDto){
      Payment payment = new Payment();   //esto lo voy a pasar un mapper para no tener tanto código aca
        payment.setUserId(request.getUserId());
        payment.setAppointmentId(request.getAppointmentId());
        payment.setShopId(request.getShopId());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.PENDING); // Estado inicial
        payment.setDescription(request.getDescription() != null ? 
                request.getDescription() : 
                "Pago por " + appointmentDto.getServiceName());
        payment.setNotes(request.getNotes());
        payment.setCardLastFour(request.getCardLastFour());
        payment.setCardHolderName(request.getCardHolderName());
        
        // Generar transaction ID único
        payment.setTransactionId(generateTransactionId());
        
        return payment;
}
  private String generateTransactionId() {
        return "PAY_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

}
