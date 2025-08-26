package com.payments.microservices.msvc_payments.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class PaymentRequest {

protected BigDecimal amount;

protected String description;

protected String externalReference;


public PaymentRequest(BigDecimal amount, String description) {
        this.amount = amount;
        this.description = description;
    }
}
