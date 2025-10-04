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
@Builder.Default
private String currency = "ARS";
private String externalReference;
private String description;

private String payerEmail;
private String payerName;
@Builder.Default
private String payerDocumentType = "DNI";
private String payerDocumentNumber;

private Long customerId;
private String callbackUrl;






    

}
