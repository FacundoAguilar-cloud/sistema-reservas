package com.payments.microservices.msvc_payments.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QRPaymentRequest {
private String transactionId;

private BigDecimal amount;

private String externalReference;

private String notificationUrl;

private String description;

private Integer expirationMinutes;

  public QRPaymentRequest(BigDecimal amount, String description) {
        this.amount = amount;
        this.description = description;
    }

}
