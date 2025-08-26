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
import com.payments.microservices.msvc_payments.request.CreditCardPaymentRequest;
import com.payments.microservices.msvc_payments.request.PaymentCreateRequest;
import com.payments.microservices.msvc_payments.request.PaymentInfoUpdateRequest;
import com.payments.microservices.msvc_payments.request.PaymentProcessingRequest;
import com.payments.microservices.msvc_payments.request.PaymentStatusUpdateRequest;
import com.payments.microservices.msvc_payments.response.PaymentProviderResponse;
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
private final PaymentValidationService paymentValidationService;
private final ExternalServiceValidation externalServiceValidation;
private final PaymentProcessingService paymentProcessingService;

//LO QUE VA EN EL SERVICIO PRINCIPAL
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
@Transactional
public PaymentResponse updatePayment(PaymentInfoUpdateRequest request, Long paymentId, Long userId) {
    Payment payment = paymentRepository.findById(paymentId)
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
public void deletePayment(Long paymentId) {
    Payment payment = paymentRepository.findById(paymentId)
    .orElseThrow(() -> new PaymentException("Payment not found, try again."));
    //validamos permisos del usuario para borrar y si realmente ese pago puede ser eliminado
    validateUserPermissions(payment, paymentId);

    paymentValidationService.validatePaymentDelete(payment);

    paymentRepository.delete(payment);
}
@Override
@Transactional
public PaymentResponse processPayment(Long paymentId){
Payment payment = paymentRepository.findById(paymentId)
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




public PaymentResponse confirmPayment(Long paymentId, String transactionId) {
    Payment payment = paymentRepository.findById(paymentId)
        .orElseThrow(() -> new ResourceNotFoundException("Payment not found."));
   if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
     log.warn("Attempt to confirm payment that is not pending. PaymentId: {}", paymentId);
     return paymentMapper.toResponseDto(payment);
   }

   payment.markAsPaid(transactionId);
   paymentRepository.save(payment);

   log.info("Payment confirmed.");

   return paymentMapper.toResponseDto(payment);
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

    private PaymentProcessingResult processPaymentByMethod (Payment payment){
        PaymentMethod method = payment.getPaymentMethod();

        try {
            switch (method) {
                 case CREDIT_CARD:
                    return processCreditCardPayment;   //estos hay que hacerlos todos, cada uno procesa un pago distinto
                   
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

    private PaymentProcessingResult processCreditCardPayment(Payment payment,  PaymentProcessingRequest processingRequest){
        CreditCardPaymentRequest request = CreditCardPaymentRequest.builder()
        .transactionId(payment.getTransactionId())
        .amount(payment.getAmount())
        .currency(payment.getCurrency())
        .cardToken(processingRequest.getCardToken())
        .cardNumber(processingRequest.getCardNumber())
        .description(payment.getDescription())
        .cardCvv(processingRequest.getCardCvv())
        .build();
        
        //llamamos al servicio extenero(REVISAR) 
        PaymentProviderResponse providerResponse = 

        return PaymentProcessingResult.builder();
        

        
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

} 

