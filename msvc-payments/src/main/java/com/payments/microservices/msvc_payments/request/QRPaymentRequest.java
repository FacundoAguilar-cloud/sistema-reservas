package com.payments.microservices.msvc_payments.request;

import java.math.BigDecimal;
import java.util.Map;

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
@Builder.Default
private String currency = "ARS";
private String externalReference;
private String notificationUrl;
private String description;
@Builder.Default
private Integer expirationMinutes = 30;

private String title;
private Long customerId;
private Map<String, String> metaData;

  public QRPaymentRequest(BigDecimal amount, String description) {
        this.amount = amount;
        this.description = description;
        this.currency = "ARS";
        this.expirationMinutes= 30;
    }

    

}
