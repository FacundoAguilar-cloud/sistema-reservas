package com.payments.microservices.msvc_payments.services;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payments.microservices.msvc_payments.config.PaymentMapper;
import com.payments.microservices.msvc_payments.entities.Payment;
import com.payments.microservices.msvc_payments.entities.PaymentMethod;
import com.payments.microservices.msvc_payments.entities.PaymentProcessingResult;
import com.payments.microservices.msvc_payments.entities.PaymentStatus;
import com.payments.microservices.msvc_payments.exceptions.InvalidPaymentMethodException;
import com.payments.microservices.msvc_payments.processing.PaymentProcessor;
import com.payments.microservices.msvc_payments.repositories.PaymentRepository;
import com.payments.microservices.msvc_payments.response.PaymentResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentProcessingService {
private final PaymentRepository paymentRepository;
private final PaymentMapper paymentMapper;
private final PaymentValidationService paymentValidationService;
private final Map<PaymentMethod, PaymentProcessor> paymentProcessors;

public PaymentResponse processPayment(Payment payment){

//validar que el pago puede ser procesado
paymentValidationService.validatePaymentForProcessing(payment);
//cambiar estatus al pago (se está procesando)
payment.setPaymentStatus(PaymentStatus.PROCESSING);
payment.setProcessingStartedAt(LocalDateTime.now());
paymentRepository.save(payment);
//procesar segun el método de pago elegido
PaymentProcessingResult result = processPaymentByMethod(payment);

//actualizar el pago con el resultado
updatePaymentFromResult(payment, result);

//guardar cambios
Payment processedPayment = paymentRepository.save(payment);

//acciones post-procesamientos
handlePostProcessingActions(processedPayment, result);

return paymentMapper.toResponseDto(processedPayment);
}

private PaymentProcessingResult processPaymentByMethod(Payment payment){
    PaymentProcessor processor = paymentProcessors.get(payment.getPaymentMethod());
    if (processor == null) {
        throw new InvalidPaymentMethodException("No processor found for payment method.");
    } 
    return processor.processPayment(payment);
}

private void handlePostProcessingActions(Payment payment, PaymentProcessingResult result){
    log.info("Payment {} processed with status: {}", payment.getId(), payment.getPaymentStatus());
}

private void updatePaymentFromResult(Payment payment, PaymentProcessingResult result){
    if (result.isSuccess()) {
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setCompletedAt(result.getProccesedAt());
    } else{
        payment.setPaymentStatus(PaymentStatus.FAILED);
    }
    if (result.getTransactionId() != null) {
        payment.setTransactionId(result.getTransactionId());
    }
    payment.setUpdatedAt(LocalDateTime.now());
}

}
