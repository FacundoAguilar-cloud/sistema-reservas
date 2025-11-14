package com.payments.microservices.msvc_payments.repositories;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.payments.microservices.msvc_payments.entities.Payment;
import com.payments.microservices.msvc_payments.entities.PaymentMethod;
import com.payments.microservices.msvc_payments.entities.PaymentStatus;


@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {
List <Payment> findPaymentByUserId(Long userId);

List <Payment>  findPaymentByAppointmentId(Long appointmentId);

List <Payment> findPaymentByShopId (Long shopId);

Optional <Payment> findByTransactionId(String transactionId);

List <Payment> findByUserIdAndPaymentStatus(Long userId, PaymentStatus paymentStatus);

List <Payment> findByUserIdAndPaymentMethod(Long userId, PaymentMethod paymentMethod);

List <Payment> findByPaymentStatus (PaymentStatus paymentStatus);

List <Payment> findPaymentsBetweenDates(LocalDate starDate, LocalDate endDate); //el problema es por esto

@Query("SELECT p FROM Payment p WHERE p.paymentStatus = 'COMPLETED' AND " +
"(p.refundAmount IS NULL OR p.refundAmount < p.amount)")
List <Payment> findRefoundablePayments();

List <Payment> findRefoundedPayments(PaymentStatus paymentStatus);

//verifica si existe un pago para una cita específica
boolean existsByAppointmentId(Long appointmentId);

Optional<Payment> findByIdempotencyKey(String idempotencyKey);

 @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
           "WHERE p.userId = :userId " +
           "AND p.paymentStatus = 'COMPLETED' " +
           "AND DATE(p.createdAt) = CURRENT_DATE")
    BigDecimal getTodaysTotalByUserId(@Param("userId") Long userId);
}


//por ahora voy a dejar estos metodos dentro del repositorio pero a partir de que siga el desarrollo se puede ir agregando más según necesitemos



