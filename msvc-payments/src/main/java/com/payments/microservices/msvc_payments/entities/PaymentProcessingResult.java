package com.payments.microservices.msvc_payments.entities;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentProcessingResult {
private boolean success;
private String transactionId;
private String providerResponse;
private String errorMessage;
private LocalDateTime proccesedAt;
private boolean requiresConfirmation;
}
