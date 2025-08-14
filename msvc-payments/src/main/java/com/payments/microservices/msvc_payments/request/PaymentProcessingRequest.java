package com.payments.microservices.msvc_payments.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public  class PaymentProcessingRequest {
 // Para tarjetas de crédito/débito
    private String cardToken; 
    private String cardNumber; // Solo para procesamiento, no se guarda
    private Integer cardExpiryMonth;
    private Integer cardExpiryYear;
    private String cardCvv; // Solo para procesamiento
    private String cardHolderName;
    
    // Para transferencias bancarias
    private String bankAccountNumber;
    private String routingNumber;
    private String bankCode;
    
    // Para wallets digitales
    private String walletToken;
    private String walletType;
    
    // Para criptomonedas
    private String cryptoAddress;
    private String cryptoType; 
    private String cryptoPrivateKey; // Solo para procesamiento
    
    // Datos adicionales
    private String twoFactorCode; // Para autenticación adicional
    private String deviceFingerprint;
}
