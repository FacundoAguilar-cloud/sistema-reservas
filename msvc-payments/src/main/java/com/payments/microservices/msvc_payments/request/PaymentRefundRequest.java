package com.payments.microservices.msvc_payments.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRefundRequest {

private BigDecimal refundAmount;

private LocalDate refundDate;

@Size(max = 400, message = "Refound reason cannot exceed 400 characters")
private String refundReason;


}
