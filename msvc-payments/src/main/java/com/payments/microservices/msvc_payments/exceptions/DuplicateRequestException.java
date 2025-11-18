package com.payments.microservices.msvc_payments.exceptions;

import com.payments.microservices.msvc_payments.response.PaymentResponse;

public class DuplicateRequestException extends RuntimeException {
 private final PaymentResponse existingPayment;

    public DuplicateRequestException (PaymentResponse existinPayment){
    super("Duplicate request detected.");
    this.existingPayment = existinPayment;
}

public PaymentResponse getExistingPayment(){ return existingPayment;}
}
