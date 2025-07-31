package com.payments.microservices.msvc_payments.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.payments.microservices.msvc_payments.entities.PaymentStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentStatusUpdateRequest {

@NotNull(message = "Payment status is mandatory")    
private PaymentStatus paymentStatus;

@Size(max = 100, message = "The id of the transaction cannot exceed 100 characters")
private String transactionId;

private LocalDate paymenDate;

private LocalTime paymentTime;

private LocalDateTime updatedAt;

}
