package com.payments.microservices.msvc_payments.services;

import java.util.List;
import com.payments.microservices.msvc_payments.request.PaymentCreateRequest;
import com.payments.microservices.msvc_payments.request.PaymentInfoUpdateRequest;
import com.payments.microservices.msvc_payments.request.PaymentStatusUpdateRequest;
import com.payments.microservices.msvc_payments.response.PaymentResponse;

public interface PaymentServiceIMPL {

PaymentResponse createPayment(PaymentCreateRequest request);

PaymentResponse getPaymentById(Long paymentId);

PaymentResponse updatePayment(PaymentInfoUpdateRequest request, Long paymentId, Long userId);

void deletePayment(Long paymentId, Long userId); 

List <PaymentResponse> getPaymentsByUserId(Long userId);

List <PaymentResponse> getPaymentsByShopId(Long shopId);

List <PaymentResponse> getPaymentsByAppointmentId(Long paymentId);

PaymentResponse processPayment(Long paymentId); //MANDA PAGO A PASARELA (VER MAS TARDE)

PaymentResponse updatePaymentStatus(PaymentStatusUpdateRequest request, Long paymentId, Long userId);

PaymentResponse confirmPayment(Long paymentId ,String transactionId);

boolean canAppointmentBePaid(Long appointmentId); //Verifica si la cita puede ser pagada

boolean paymentExistsForAppointment(Long appointmentId); 

PaymentResponse getPaymentByIdempotencyKey(String idempotencyKey);


}
