package com.payments.microservices.msvc_payments.config;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.payments.microservices.msvc_payments.dto.AppointmentDto;
import com.payments.microservices.msvc_payments.entities.Payment;
import com.payments.microservices.msvc_payments.entities.PaymentStatus;
import com.payments.microservices.msvc_payments.request.PaymentCreateRequest;
import com.payments.microservices.msvc_payments.response.PaymentResponse;
@Component
public class PaymentMapper {
//se movio al mapper a una clase exclusiva para hacer el service mas limpio    
public Payment toEntity(PaymentCreateRequest request, AppointmentDto appointmentDto){
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

    public PaymentResponse toResponseDto(Payment payment) {
        // TODO: Usar PaymentMapper cuando lo creemos
        PaymentResponse dto = new PaymentResponse();
        dto.setId(payment.getId());
        dto.setUserId(payment.getUserId());
        dto.setAppointmentId(payment.getAppointmentId());
        dto.setShopId(payment.getShopId());
        dto.setAmount(payment.getAmount());
        dto.setCurrency(payment.getCurrency());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setTransactionId(payment.getTransactionId());
        dto.setDescription(payment.getDescription());
        dto.setPaymentDate(payment.getPaymenDate()); // Nota: tienes el typo aquí también
        dto.setPaymentTime(payment.getPaymentTime());
        dto.setNotes(payment.getNotes());
        dto.setCardLastFour(payment.getCardLastFour());
        dto.setCardHolderName(payment.getCardHolderName());
        dto.setCreatedAt(payment.getCreatedAt());
        
        return dto;
    }
     public List<PaymentResponse> toResponseDtoList(List<Payment> payments) {
        return payments.stream()
                .map(this::toResponseDto)
                .toList();
    }

}
