package com.payments.microservices.msvc_payments.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

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
    private String qrCode; // Para pagos QR
    private String paymentUrl; // URL de pago
    private String status; // PENDING, APPROVED, REJECTED, etc.
    private String message;
    private String errorCode;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime processedAt;
    private Map<String, String> metadata;




    




//quizas falta completar con algo más pero por el momento va a quedar así, en caso de faltar algo se irá agregando a medida que se desarrolla la app.

}


