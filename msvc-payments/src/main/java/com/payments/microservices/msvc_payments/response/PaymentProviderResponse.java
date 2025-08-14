package com.payments.microservices.msvc_payments.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentProviderResponse {
private boolean success;

private String transactionId;

private String AuthCode;

private String errorMsg;

private String errorCode;

private String response;

//TERMINAR, ESTÁ INCOMPLETO.
}
