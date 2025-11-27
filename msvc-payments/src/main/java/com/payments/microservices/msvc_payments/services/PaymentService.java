package com.payments.microservices.msvc_payments.services;



import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.payments.microservices.msvc_payments.client.AppointmentClient;
import com.payments.microservices.msvc_payments.client.ShopClient;
import com.payments.microservices.msvc_payments.client.UserClient;
import com.payments.microservices.msvc_payments.config.PaymentMapper;
import com.payments.microservices.msvc_payments.dto.AppointmentDto;
import com.payments.microservices.msvc_payments.dto.AppointmentStatus;
import com.payments.microservices.msvc_payments.entities.Payment;
import com.payments.microservices.msvc_payments.entities.PaymentStatus;
import com.payments.microservices.msvc_payments.exceptions.PaymentException;
import com.payments.microservices.msvc_payments.exceptions.ResourceNotFoundException;
import com.payments.microservices.msvc_payments.repositories.PaymentRepository;
import com.payments.microservices.msvc_payments.request.PaymentCreateRequest;
import com.payments.microservices.msvc_payments.request.PaymentInfoUpdateRequest;
import com.payments.microservices.msvc_payments.request.PaymentStatusUpdateRequest;
import com.payments.microservices.msvc_payments.response.PaymentResponse;
import com.payments.microservices.msvc_payments.security.services.IdempotencyService;
import com.payments.microservices.msvc_payments.security.validators.PaymentAmountValidator;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Builder
@RequiredArgsConstructor
@Slf4j
public class PaymentService implements PaymentServiceIMPL {
private final AppointmentClient appointmentClient;
private final UserClient userClient;
private final ShopClient shopClient;
private final PaymentRepository paymentRepository;
private final PaymentMapper paymentMapper;
private final PaymentValidationService paymentValidationService;
private final ExternalServiceValidation externalServiceValidation;
private final PaymentProcessingService paymentProcessingService;
private final PaymentAuthorizationService paymentAuthorizationService;
private final IdempotencyService idempotencyService;
private final PaymentAmountValidator paymentAmountValidator;

//LO QUE VA EN EL SERVICIO PRINCIPAL
@Override
public PaymentResponse createPayment(PaymentCreateRequest request, String idempotencyKey, String clientIp, String userAgent) {
    
    Optional <Payment> existingPayment = idempotencyService.findByIdIdempotencyKey(idempotencyKey);

    if (existingPayment.isPresent()) {
        log.info("Returning existing payment for idempotency key.", idempotencyKey);

        return paymentMapper.toResponseDto(existingPayment.get());
    }
    
    log.info("Creating payment for appointment: {}", request.getAppointmentId());
        
        // usamos servicio de validacion externa
        ValidationContext validationContext = externalServiceValidation.validateExternalEntities(request);
        // validamos logica de negocio
        paymentValidationService.validatePaymentCreation(request, validationContext.getAppointment());

        paymentAmountValidator.validateAmount(request.getAmount());
        paymentAmountValidator.validateAmountMatchesAppointment(request.getAmount(), validationContext.getAppointment().getServicePrice());

        BigDecimal todaysTotal = paymentRepository.getTodaysTotalByUserId(request.getUserId());
        paymentAmountValidator.validateDayLimit(request.getAmount(), todaysTotal);

        AppointmentDto appointment = appointmentClient.getAppointmentById(request.getAppointmentId());

        
        
        // creamos y guardamos pago
        Payment payment = paymentMapper.toEntity(request, validationContext.getAppointment());
        payment.setIdempotencyKey(idempotencyKey);
        payment.setClientIp(clientIp);
        payment.setUserAgent(userAgent);
        payment.setExpiresAt(LocalDateTime.now().plusHours(24));
        payment.setProcessingAttempts(0);

        Payment savedPayment = paymentRepository.save(payment);
        
        log.info("Payment created successfully with ID: {}", savedPayment.getId());
        return paymentMapper.toResponseDto(savedPayment);

    
}

@Override
public PaymentResponse getPaymentById(Long id) {
  Payment payment = paymentRepository.findById(id)
  .orElseThrow(() -> new ResourceNotFoundException("Payment not found."));
  return paymentMapper.toResponseDto(payment);
}
@Override
@Transactional
public PaymentResponse updatePayment(PaymentInfoUpdateRequest request, Long id, Long userId) {
    Payment payment = paymentRepository.findById(id)
    .orElseThrow(() -> new ResourceNotFoundException("Payment not found."));
    //deberiamos de tener un metodo que se ocupe de verificar los permisos del usuario para poder hacer cambios (admin o owner), deberia hacerlo el security mas adelante
    validateUserPermissions(payment, userId);
    
    if (request.getCardHolderName() != null) {
        payment.setCardHolderName(request.getCardHolderName());
    }
    if (request.getCardLastFour() != null) {
        payment.setCardLastFour(request.getCardLastFour());
    }
    if (request.getDescription() != null ) {
        payment.setDescription(request.getDescription());
    }
    if (request.getNotes() != null) {
        payment.setNotes(request.getNotes());
    }

    Payment paymentInfoUpdated = paymentRepository.save(payment);


    return paymentMapper.toResponseDto(paymentInfoUpdated);
    
}
@Override
@Transactional
public void deletePayment(Long id, Long userId) {
    Payment payment = paymentRepository.findById(id)
    .orElseThrow(() -> new PaymentException("Payment not found, try again."));
    //validamos permisos del usuario para borrar y si realmente ese pago puede ser eliminado
  paymentAuthorizationService.validateUserPermissions(payment, userId);

  paymentValidationService.validatePaymentDelete(payment);

  paymentRepository.delete(payment);

    log.info("Payment deleted", id, userId);
}
@Override
@Transactional
public PaymentResponse processPayment(Long id){
Payment payment = paymentRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

return paymentProcessingService.processPayment(payment);
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
public List<PaymentResponse> getPaymentsByAppointmentId(Long id) {
   List <Payment> payments = paymentRepository.findPaymentByAppointmentId(id);
   return paymentMapper.toResponseDtoList(payments);
}

@Override
public PaymentResponse updatePaymentStatus(PaymentStatusUpdateRequest request, Long id, Long userId) {
    Payment payment = paymentRepository.findById(id)
    .orElseThrow(()->  new ResourceNotFoundException("Payment not found, please try again."));
    //validamos usuario
    validateUserPermissions(payment, userId);

    //validar que el status que vamos a cambiar sea correcto
    paymentValidationService.validateStatusChange(request.getPaymentStatus(), payment.getPaymentStatus());

    payment.setPaymentStatus(request.getPaymentStatus());
    payment.setUpdatedAt(LocalDateTime.now());

    if (request.getTransactionId() != null) {
        payment.setTransactionId(request.getTransactionId());
    }

    if (request.getPaymentDate() != null) {
        payment.setPaymenDate(request.getPaymentDate());
    }

    if (request.getPaymentTime() != null) {
        payment.setPaymentTime(request.getPaymentTime());
    }

    switch (request.getPaymentStatus()) {
        case FAILED:
            if (request.getFailureReason() != null) {
                payment.setRefundAmount(request.getRefoundAmount());
            }
            break;
            case REFUNDED:
            if (request.getRefoundAmount() != null) {
                payment.setRefundAmount(request.getRefoundAmount());
            }
            payment.setRefundDate(LocalDate.now());
            break;
    
            case COMPLETED:
            payment.setCompletedAt(LocalDateTime.now());
        default:
            break;
    }
    if (request.getExternalReference() != null) {
        payment.setExternalReference(request.getExternalReference());
    }

    Payment updatedPayment = paymentRepository.save(payment);

    return paymentMapper.toResponseDto(updatedPayment);
}




public PaymentResponse confirmPayment(Long id, String transactionId) {
    Payment payment = paymentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Payment not found."));
   if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
     log.warn("Attempt to confirm payment that is not pending. PaymentId: {}", id);
     return paymentMapper.toResponseDto(payment);
   }

   payment.markAsPaid(transactionId);
   paymentRepository.save(payment);

   log.info("Payment confirmed.");

   return paymentMapper.toResponseDto(payment);
}

public PaymentResponse confirmPaymentForWebhook(Long id, String transactionId) {
    Payment payment = paymentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Payment not found."));
    
    if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
        log.warn("Attempt to confirm non-pending payment: {}", id);
        return paymentMapper.toResponseDto(payment);
    }

    payment.markAsPaid(transactionId.toString());
    paymentRepository.save(payment);

    log.info("Payment {} confirmed via webhook", id);
    return paymentMapper.toResponseDto(payment);
}
@Override
public boolean canAppointmentBePaid(Long appointmentId) { 
    boolean paymentExists = paymentRepository.existsByAppointmentId(appointmentId);

    if (paymentExists) {
        log.info("Payment already exists for appointment.", appointmentId);
        return false;
    }

    try {
        AppointmentDto appointment = appointmentClient.getAppointmentById(appointmentId);

        if (appointment == null ) {
            log.warn("Appointment not found.", appointmentId);
            return false;
        }

        if (appointment.getStatus() != AppointmentStatus.PENDING && 
        appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            log.info("Appointment has invalid status for payment", appointmentId, appointment.getStatus());
            return false;
        }

        if (appointment.getCancelledAt() != null) {
            log.info("Appointment is cancelled.", appointmentId);
            return false;
        }
        if (appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            log.info("appointment is in the past.", appointmentId);
            return false;
        }
        log.debug("Appointment can be paid.", appointmentId);
        return true;
    } catch (Exception e) {
        log.error("Error checking if appointment can be pais", appointmentId, e);
        return false;
    }
}

  public void validateUserPermissions(Payment payment,Long userId){
    if (payment.getUserId().equals(userId)) {
        return;
    }

  

  //verificar si el barbero asociado  al pago es de la cita en concreto
Map<String, Object> appointmentData = (Map<String, Object>) appointmentClient.getAppointmentById(payment.getAppointmentId());
   if (appointmentData != null) {
    Long barberId = (Long) appointmentData.get("barberId");
    if (barberId != null && barberId.equals(userId)) {
        return;
    }
   }
   throw new PaymentException("You dont have permissions to access this payment.");
  }

  @Override
  public PaymentResponse getPaymentByIdempotencyKey(String idempotencyKey) {
   Payment payment = idempotencyService.findByIdIdempotencyKey(idempotencyKey).orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
   return paymentMapper.toResponseDto(payment);
  }

  public void validateUserOwnership(Long id, Long userId){
    Payment payment = paymentRepository.findById(id)
    .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    paymentAuthorizationService.validateUserPermissions(payment, userId);
  }

  public void validateShopOwnership(Long shopId, Long userId){
    Map <String, Object> shopData = shopClient.getShopById(shopId);
    if (shopData != null) {
        Long ownerId = (Long) shopData.get("ownerId");
        if (!ownerId.equals(userId)) {
           throw new SecurityException("User is not the shop owner.");
        }
   }  
  }

  public void validateUserAppointmentOwnership(Long appointmentId, Long userId){
    AppointmentDto appointment = appointmentClient.getAppointmentById(appointmentId);

    if (appointment != null && !appointment.getClientId().equals(userId)) {
        throw new SecurityException("User is not the appointment owner.");
    }
  }

} 

