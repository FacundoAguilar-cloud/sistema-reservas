package com.payments.microservices.msvc_payments.repositories;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.payments.microservices.msvc_payments.entities.Payment;
import com.payments.microservices.msvc_payments.entities.PaymentMethod;
import com.payments.microservices.msvc_payments.entities.PaymentStatus;


@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {
List <Payment> findPaymentByUserId(Long userId);

Optional <Payment> findByPaymentId(Long paymentId);

List <Payment>  findPaymentByAppointmentId(Long appointmentId);

List <Payment> findPaymentByShopId (Long shopId);

Optional <Payment> findByTransactionId(Long transactionId);

List <Payment> findByUserIdAndPaymentStatus(Long userId, PaymentStatus paymentStatus);

List <Payment> findByUserIdAndPaymentMethod(Long userId, PaymentMethod paymentMethod);

List <Payment> findByPaymentStatus (PaymentStatus paymentStatus);

List <Payment> findPaymentsBetweenDates(LocalDate starDate, LocalDate endDate);

@Query("SELECT p FROM Payment p WHERE p.paymentStatus = 'COMPLETED' AND " +
"(p.refundAmount IS NULL OR p.refundAmount < p.amount)")
List <Payment> findRefoundablePayments();

List <Payment> findRefoundedPayments(PaymentStatus paymentStatus);

//verifica si existe un pago para una cita específica
boolean existsByAppointmentId(Long appointmentId);


//por ahora voy a dejar estos metodos dentro del repositorio pero a partir de que siga el desarrollo se puede ir agregando más según necesitemos


}
