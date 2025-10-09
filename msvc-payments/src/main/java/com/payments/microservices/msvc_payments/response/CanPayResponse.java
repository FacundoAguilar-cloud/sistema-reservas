package com.payments.microservices.msvc_payments.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CanPayResponse {
private Long appointmentId;
private boolean canPay;
private String paymentMethod;
private String message;
}
