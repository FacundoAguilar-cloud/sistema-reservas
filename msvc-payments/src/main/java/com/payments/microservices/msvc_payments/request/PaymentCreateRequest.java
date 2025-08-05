package com.payments.microservices.msvc_payments.request;

import java.math.BigDecimal;
import com.payments.microservices.msvc_payments.entities.PaymentMethod;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCreateRequest {

private Long userId;

private Long appointmentId;

private Long shopId;

private BigDecimal amount;

private String currency;

private PaymentMethod paymentMethod;

private String notes;

private String description;

@Size(max = 4, message = "Card last four cannot exceed 4 characters")
private String cardLastFour;

@Size(max = 30, message = "Cardholder name cannot exceed 30 characters")
private String cardHolderName;

}
