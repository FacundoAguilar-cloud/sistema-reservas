package com.payments.microservices.msvc_payments.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payments.microservices.msvc_payments.client.AppointmentClient;
import com.payments.microservices.msvc_payments.client.ShopClient;
import com.payments.microservices.msvc_payments.client.UserClient;
import com.payments.microservices.msvc_payments.config.PaymentMapper;
import com.payments.microservices.msvc_payments.dto.AppointmentDto;
import com.payments.microservices.msvc_payments.dto.AppointmentStatus;
import com.payments.microservices.msvc_payments.dto.ShopDto;
import com.payments.microservices.msvc_payments.dto.UserDto;
import com.payments.microservices.msvc_payments.entities.Payment;
import com.payments.microservices.msvc_payments.entities.PaymentMethod;
import com.payments.microservices.msvc_payments.entities.PaymentProcessingResult;
import com.payments.microservices.msvc_payments.entities.PaymentStatus;
import com.payments.microservices.msvc_payments.exceptions.InvalidPaymentMethodException;
import com.payments.microservices.msvc_payments.exceptions.InvalidPaymentStatusException;
import com.payments.microservices.msvc_payments.exceptions.PaymentException;
import com.payments.microservices.msvc_payments.exceptions.PaymentProcessingException;
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
    throw new IllegalArgumentException("Shop not found");
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

//validaciones para la lógica del negocio 


validateIfUserCanPayAppointment(request.getUserId(), appointment); //OK

validateIfShopMatch(request.getShopId(), request.getShopId());    //OK

validateIfPaymentDoesNotExist(request.getAppointmentId());        //OK

validatePaymentMethod(request); //OK


Payment payment = paymentMapper.toEntity(request, appointment);

Payment savedPayment = paymentRepository.save(payment);

return paymentMapper.toResponseDto(savedPayment);
}
@Override
public PaymentResponse getPaymentById(Long paymentId) {
  Payment payment = paymentRepository.findById(paymentId)
  .orElseThrow(() -> new ResourceNotFoundException("Payment not found."));
  return paymentMapper.toResponseDto(payment);
}
@Override
public PaymentResponse updatePayment(PaymentInfoUpdateRequest request, Long paymentId, Long userId) {
    Payment payment = paymentRepository.findById(paymentId)
    .orElseThrow(() -> new ResourceNotFoundException("Payment not found."));
    //deberiamos de tener un metodo que se ocupe de verificar los permisos del usuario para poder hacer cambios (admin o owner)
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
public void deletePayment(Long paymentId) {
    Payment payment = paymentRepository.findById(paymentId)
    .orElseThrow(() -> new PaymentException("Payment not found, try again."));
    //validamos permisos del usuario para borrar y si realmente ese pago puede ser eliminado
    validateUserPermissions(payment, paymentId);

    validatePaymentDelete(payment);

    paymentRepository.delete(payment);

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
public PaymentResponse updatePaymentStatus(PaymentStatusUpdateRequest request, Long paymentId, Long userId) {
    Payment payment = paymentRepository.findById(paymentId)
    .orElseThrow(()->  new ResourceNotFoundException("Payment not found, please try again."));
    //validamos usuario
    validateUserPermissions(payment, userId);

    //validar que el status que vamos a cambiar sea correcto
    validateStatusChange(request.getPaymentStatus(), payment.getPaymentStatus());

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

@Override
@Transactional
public PaymentResponse processPayment(Long paymentId) { //falta esto
    try {
       //Obtener el pago
       Payment payment = paymentRepository.findById(paymentId)
       .orElseThrow(()-> new ResourceNotFoundException("Payment not found"));

        //Verificar si el pago está disponible para ser procesado correctamente 
        validatePaymentForProccesing(payment);  //HACER

        // Dejamos el status en PROCESSING
        payment.setPaymentStatus(PaymentStatus.PROCESSING);
        payment.setProcessingStartedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        //se procesa acá segun el metodo de pagó utilizado 

        PaymentProcessingResult result = processPaymentByMethod(payment); //estos metodos tambien los vamos a tener que crear

        //actualizamos el pago

        updatePaymentFromResult(payment, result);

        //guardamos los cambios
        Payment processedPayment = paymentRepository.save(payment);

        //acciones post-procesamiento

        handlePostProcessingActions(processedPayment, result);

        return paymentMapper.toResponseDto(processedPayment);

    } catch (PaymentProcessingException e) {
        throw new PaymentProcessingException("Unexpected error during payment processing");
    }
}

@Override
public PaymentResponse confirmPayment(String transactionId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'confirmPayment'"); //falta esto
}
@Override
public boolean canAppointmentBePaid(Long appointmentId) { //falta esto
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'canAppointmentBePaid'");
}
@Override
public boolean paymentExistsForAppointment(Long appointmentId) { //falta esto
    // TODO Auto-generated method stub 
    throw new UnsupportedOperationException("Unimplemented method 'paymentExistsForAppointment'");
}
//validaciones para logica de negocio
private void validateIfUserCanPayAppointment(Long userId, AppointmentDto appointmentDto){
    if (!appointmentDto.getClientId().equals(userId)) {
        throw new SecurityException("User is not authorized to pay for the appointment.");
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
  

private void validateIfShopMatch(Long shopId, Long appointmentShopId) { //falta esto
    if (!shopId.equals(appointmentShopId)) {
        throw new IllegalArgumentException("Shop ID does not match the appointment's shop.");
    }
}

private void validateIfPaymentDoesNotExist(Long appointmentId){ //falta esto
    if (!paymentRepository.existsByAppointmentId(appointmentId)) {
        throw new IllegalStateException("Payment already exist for this appointment.");
    }
}

private void validatePaymentForProccesing(Payment payment){ 
    if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
        throw new InvalidPaymentStatusException("Payment cannot be processed.");
    }
   
    //validamos si el pago expiró o no 
    if (payment.getExpiresAt() != null && payment.getExpiresAt().isBefore(LocalDateTime.now())) {
        throw new PaymentException("Payment has expired and cannot be processed.");
    }

    //aseguramos que el monto no sea 0) 
    if (payment.getAmount() == null || payment.getAmount().compareTo(BigDecimal.ZERO) <= 0){
        throw new PaymentException("Payment amount must be grater than zero.");
    }

    //validamos el metodo de pago
    if (payment.getPaymentMethod() == null) {
        throw new InvalidPaymentMethodException("Payment method is required.");
    }}

    private PaymentProcessingResult processPaymentByMethod (Payment payment){
        PaymentMethod method = payment.getPaymentMethod();

        try {
            switch (method) {
                case CREDIT_CARD:
                    return processCreditCardPayment; //estos hay que hacerlos todos, cada uno procesa un pago distinto
                 case DEBIT_CARD:
                    return processDebitCardPayment;
                 case BANK_TRANSFER:
                 return processBankTransferPayment;
                 case DIGITAL_WALLET:
                 return processDigitalWalletPayment;      
                 case CRYPTO:
                 return processCryptoPayment;   
                default:
                    throw new InvalidPaymentMethodException("Unsupported payment method.");
            }
        } catch (PaymentException e) {
            throw new PaymentException("Service unavailable.");
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
//Validaciones para updates/deletes y demás

  public void validateUserPermissions(Payment payment,Long userId){
    if (payment.getUserId().equals(userId)) {
        return;
    }
//verificar donde se realizó el pago (en que tienda basicamete) //REVISAR
  Map <String, Object> shopData = shopClient.  getShopById(payment.getShopId());
    if (shopData != null) {
        Long ownerId = (Long) shopData.get("ownerId");
        if (ownerId != null && ownerId.equals(userId)) {
            return;
        }
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

