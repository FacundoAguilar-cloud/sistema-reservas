package com.payments.microservices.msvc_payments.processing;

import com.payments.microservices.msvc_payments.entities.Payment;
import com.payments.microservices.msvc_payments.entities.PaymentMethod;
import com.payments.microservices.msvc_payments.entities.PaymentProcessingResult;

public class QRPaymentProcessor extends BasePaymentProcessor {

    @Override
    public PaymentProcessingResult processPayment(Payment payment) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processPayment'");
    }

    @Override
    public PaymentMethod getSupportedMethod() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSupportedMethod'");
    }

}
