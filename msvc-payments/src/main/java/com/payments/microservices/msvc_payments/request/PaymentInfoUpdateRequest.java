package com.payments.microservices.msvc_payments.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentInfoUpdateRequest {

@Size(max = 200, message = "The description cannot exceed 200 characters")    
private String description;

@Size(max = 300, message = "The notes cannot exceed 300 characters")
private String notes;

@Size(max = 4, message = "Cannot exceed 4 numbers.")
private String cardLastFour;

@Size(max = 30, message = "Name cannot exceed 30 characters")
private String cardHolderName;
}
