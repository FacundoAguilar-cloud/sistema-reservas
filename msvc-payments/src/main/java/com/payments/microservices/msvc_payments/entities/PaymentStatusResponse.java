package com.payments.microservices.msvc_payments.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentStatusResponse {
private String paymentId;
private PaymentStatus status;
private BigDecimal amount;
private LocalDateTime createdAt;
private boolean isPending;
private boolean isCompleted;
}
