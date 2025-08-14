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
public class DigiWalletPaymentRequest {
private String transactionId;

private BigDecimal amount;

private String currency;

private String walletToken;

private String walletType;

private String description;

private Long customerId;

}
