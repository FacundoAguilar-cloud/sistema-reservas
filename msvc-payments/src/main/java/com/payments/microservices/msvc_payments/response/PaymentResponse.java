package com.payments.microservices.msvc_payments.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.payments.microservices.msvc_payments.entities.PaymentMethod;
import com.payments.microservices.msvc_payments.entities.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

private Long id;

private Long userId;

private Long appointmentId;

private Long shopId;

private BigDecimal amount;

private String currency;

private PaymentMethod paymentMethod;

private PaymentStatus paymentStatus;

private String transactionId;

private String description;

private LocalDate paymentDate;

private LocalTime paymentTime;

private String notes;

private String cardLastFour;

private String cardHolderName;

private LocalDateTime createdAt;

private String paymentUrl;

}
