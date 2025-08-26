package com.payments.microservices.msvc_payments.request;

import java.math.BigDecimal;

import com.payments.microservices.msvc_payments.entities.PaymentMethod;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class CashPaymentRequest extends PaymentRequest {
private String transactionId;

private BigDecimal amount;

private String currency;

private String description;

private Long customerId;

private String bankAccount;

private PaymentMethod cashtype; //si es efectivo o transferencia bancaria 
}
