package com.payments.microservices.msvc_payments.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundRequest {
private Long userId;
private BigDecimal amount;
private String reason;
}
