package com.payments.microservices.msvc_payments.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.payments.microservices.msvc_payments.dto.AppointmentDto;
import com.payments.microservices.msvc_payments.dto.AppointmentStatus;
import com.payments.microservices.msvc_payments.entities.Payment;
import com.payments.microservices.msvc_payments.entities.PaymentMethod;
import com.payments.microservices.msvc_payments.entities.PaymentStatus;
import com.payments.microservices.msvc_payments.exceptions.InvalidPaymentMethodException;
import com.payments.microservices.msvc_payments.exceptions.InvalidPaymentStatusException;
import com.payments.microservices.msvc_payments.exceptions.PaymentException;
import com.payments.microservices.msvc_payments.repositories.PaymentRepository;
import com.payments.microservices.msvc_payments.request.PaymentCreateRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentValidationService {
private final PaymentRepository paymentRepository;

public void validatePaymentCreation(PaymentCreateRequest request, AppointmentDto appointment){
validateIfUserCanPayAppointment(request.getUserId(), appointment);
validateIfShopMatch(request.getShopId(), appointment.getShopId());
validateIfPaymentDoesNotExist(request.getAppointmentId());
validateIfAppointmentCanBePaid(appointment);
validatePaymentMethod(request);
}

public void validatePaymentForProcessing(Payment payment){
      if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new InvalidPaymentStatusException("Payment cannot be processed.");
        }

        if (payment.getExpiresAt() != null && payment.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new PaymentException("Payment has expired and cannot be processed.");
        }

        if (payment.getAmount() == null || payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentException("Payment amount must be greater than zero.");
        }

        if (payment.getPaymentMethod() == null) {
            throw new InvalidPaymentMethodException("Payment method is required.");
        }
}


private void validateIfUserCanPayAppointment(Long userId, AppointmentDto appointmentDto){ 
    if (!appointmentDto.getClientId().equals(userId)) {
        throw new SecurityException("User is not authorized to pay for the appointment.");
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

private void validateIfAppointmentCanBePaid(AppointmentDto appointment){
    if (appointment.getStatus() != AppointmentStatus.PENDING &&
    appointment.getStatus() != AppointmentStatus.CONFIRMED) {
        throw new IllegalStateException("Appointment cannot be paid, check the status and wait.");
    }
    if (appointment.getCancelledAt() != null) {
        throw new IllegalStateException("Cannot pay for cancelled appointments.");
    }

    if (appointment.getAppointmentDate().isBefore(LocalDate.now())) {
        throw new IllegalStateException("Cannot pay for past appointments.");
    }
 }

 private void validatePaymentMethod(PaymentCreateRequest request){ 
if (request.getPaymentMethod() == PaymentMethod.CREDIT_CARD ||
request.getPaymentMethod() == PaymentMethod.DEBIT_CARD) {
    if (request.getCardLastFour() == null || request.getCardLastFour().trim().isEmpty()) {
        throw new IllegalArgumentException("Card last four digit are required for payment.");
    }
    if (request.getCardHolderName() == null || request.getCardHolderName().trim().isEmpty()) {
        throw new IllegalArgumentException("Card holder name is required for payment.");
    }
}
}

 public void validatePaymentDelete(Payment payment){ 
    if ( payment.getPaymentStatus() == PaymentStatus.COMPLETED  ||
         payment.getPaymentStatus() == PaymentStatus.PROCESSING ||
         payment.getPaymentStatus() == PaymentStatus.REFUNDED) {
        throw new PaymentException("You cannot delete a completed or processing payment");
    }
   }

   public void validateStatusChange(PaymentStatus actualStatus, PaymentStatus newStatus){ 
    if (actualStatus == PaymentStatus.COMPLETED || newStatus == PaymentStatus.COMPLETED) {
        throw new PaymentException("You cannot change the status of a completed payment.");
    }
   }









}
