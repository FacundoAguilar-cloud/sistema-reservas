package com.payments.microservices.msvc_payments.security.validators;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.payments.microservices.msvc_payments.exceptions.PaymentException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PaymentAmountValidator {

@Value("${payment.min-amount:100}")    
private BigDecimal minimalAmount;
@Value("${payment.max-amount:1000000}")  
private BigDecimal maxAmount;
@Value("${payment.max-daily-amount:5000000}")  
private BigDecimal maxDailyAmount;


public void validateAmount(BigDecimal amount){
    if (amount == null) {
        throw new PaymentException("Payment amount cannot be null.");
    }

    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        throw new PaymentException("Amount must be greater than zero.");
    }

    if (amount.compareTo(minimalAmount) < 0) {
        log.warn("Minimal amount is below minimun", amount, minimalAmount);

        throw new PaymentException("Payment amount is below minimun allowed");
    }

    if (amount.compareTo(maxAmount) > 0) {
        log.warn("Payment amount exceeds maximum allowed" , amount, maxAmount);
        
        throw new PaymentException("Payment amount is greater than max allowed.");
    }

    if (amount.scale() > 2) {
        throw new PaymentException("Payment amount cannot have more than 2 decimal places.");
        }
        log.debug("Amount validation passed.", amount);
    }


}



