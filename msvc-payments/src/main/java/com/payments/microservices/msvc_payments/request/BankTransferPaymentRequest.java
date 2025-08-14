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
public class BankTransferPaymentRequest {
private String transactionId;

private BigDecimal amount;

private String currency;

private String description;

private Long customerId;

private String bankAccount;
}
