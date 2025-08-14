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
public class CryptoPaymentRequest {
private String transactionId;

private BigDecimal amount;

private String currency;

private String cryptoadress;

private String cryptoType;

private String description;

private Long customerId;


}
