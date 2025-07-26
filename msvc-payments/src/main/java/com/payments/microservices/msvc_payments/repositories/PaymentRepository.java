package com.payments.microservices.msvc_payments.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.payments.microservices.msvc_payments.entities.Payment;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {

}
