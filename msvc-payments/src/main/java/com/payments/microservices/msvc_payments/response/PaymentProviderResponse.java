package com.payments.microservices.msvc_payments.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

private BigDecimal amount;

private BigDecimal processedAmount;
 
private String currency;

private BigDecimal fee;   
          
private BigDecimal netAmount;

private LocalDateTime processedAt;

private LocalDateTime requestedAt;

private String providerTransactionId;

private String providerName;

private String errorCategory;

private String errorDescription;

private String maskedCardNumber;

private String cardBrand;

private String cardType;

public boolean hasCardInfo() {
        return maskedCardNumber != null || cardBrand != null || cardType != null;
    }

public boolean isSuccessful() {
        return success && transactionId != null && !transactionId.trim().isEmpty();
    }

    




//quizas falta completar con algo más pero por el momento va a quedar así, en caso de faltar algo se irá agregando a medida que se desarrolla la app.

}


