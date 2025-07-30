package com.payments.microservices.msvc_payments.repositories;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.payments.microservices.msvc_payments.entities.Payment;
import com.payments.microservices.msvc_payments.entities.PaymentMethod;
import com.payments.microservices.msvc_payments.entities.PaymentStatus;


@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {
List <Payment> findPaymentByUserId(Long userId);

List <Payment>  findPaymentByAppointmentId(Long appointmentId);

List <Payment> findPaymentByShopId (Long shopId);

Optional <Payment> findByTransactionId(Long transactionId);

List <Payment> findByUserIdAndPaymentStatus(Long userId, PaymentStatus paymentStatus);

List <Payment> findByUserIdAndPaymentMethod(Long userId, PaymentMethod paymentMethod);

List <Payment> findByPaymentStatus (PaymentStatus paymentStatus);

List <Payment> findPaymentsBetweenDates(LocalDate starDate, LocalDate endDate);

//aca falta seguir agregando querys mas complejas donde se muestren consultas de reportes, estadisticas y reembolsos o con consultas complejas y validaciones


}
