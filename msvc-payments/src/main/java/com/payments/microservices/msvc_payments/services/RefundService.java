package com.payments.microservices.msvc_payments.services;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payments.microservices.msvc_payments.config.PaymentMapper;
import com.payments.microservices.msvc_payments.entities.Payment;
import com.payments.microservices.msvc_payments.entities.PaymentMethod;
import com.payments.microservices.msvc_payments.entities.PaymentStatus;
import com.payments.microservices.msvc_payments.exceptions.PaymentException;
import com.payments.microservices.msvc_payments.exceptions.ResourceNotFoundException;
import com.payments.microservices.msvc_payments.repositories.PaymentRepository;
import com.payments.microservices.msvc_payments.response.MercadoPagoCardProvider;
import com.payments.microservices.msvc_payments.response.PaymentProviderResponse;
import com.payments.microservices.msvc_payments.response.PaymentResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RefundService {
private final PaymentRepository paymentRepository;
private final MercadoPagoCardProvider mercadoPagoCardProvider;
private final PaymentAuthorizationService paymentAuthorizationService;
private final PaymentMapper paymentMapper;



public PaymentResponse processFullRefund(Long paymentId, Long requestingUserId, String reason){
log.info("Processing full refund for payment.", paymentId);

Payment payment = paymentRepository.findById(paymentId).orElseThrow( () -> new ResourceNotFoundException("Payment not found."));

paymentAuthorizationService.validateUserPermissions(payment, requestingUserId);
validateRefundEligibility(payment);

PaymentProviderResponse refundResponse = mercadoPagoCardProvider.refundPayment(payment.getProviderTransactionId(), null);

if (refundResponse.isSuccess()) {
    payment.setPaymentStatus(PaymentStatus.REFUNDED);
    payment.setRefundAmount(payment.getAmount());
    payment.setRefundDate(LocalDate.now());

    if (reason != null) {
        payment.getMetadata().put("refund_reason", reason);
    }

    payment = paymentRepository.save(payment);
    return paymentMapper.toResponseDto(payment);
    

    }
    else{
        throw new PaymentException("Refund failed"+ refundResponse.getMessage());
    }
}




private void validateRefundEligibility(Payment payment){
    if (payment.getPaymentMethod() != PaymentMethod.CREDIT_CARD && payment.getPaymentMethod() != PaymentMethod.DEBIT_CARD) {
        throw new PaymentException("Only card payments can be refunded.");
    }

    if (payment.getPaymentStatus() != PaymentStatus.COMPLETED) {
        throw new PaymentException("Only completed payments can be refunded.");
    }
}

}

