package com.payments.microservices.msvc_payments.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QrGenerationResponse {
private BigDecimal amount;
private String description;
private String externalReference;
private Integer expirationMinutes;
}
