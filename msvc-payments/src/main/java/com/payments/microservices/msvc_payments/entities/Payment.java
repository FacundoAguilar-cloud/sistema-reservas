package com.payments.microservices.msvc_payments.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payments")
public class Payment {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private BigDecimal amount;

private String currency;

private PaymentMethod paymentMethod;

private PaymentStatus paymentStatus;

private Long transactionId;

private String description;

private LocalDate paymenDate;

private LocalTime paymentTime;

private String notes;


}
