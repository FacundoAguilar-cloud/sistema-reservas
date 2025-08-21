package com.payments.microservices.msvc_payments.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

@NotNull(message = "User id is mandatory.")
@Column(name = "user_id", nullable = false)
private Long userId;

private Long customerId;

@NotNull(message = "Appointment id is mandatory")
@Column(name = "appointment_id", nullable = false)
private Long appointmentId;

@NotNull(message = "Shop id is mandatory")
@Column(name = "shop_id", nullable = false)
private Long shopId;

@NotNull(message = "Amount is mandatory")
@DecimalMin(value = "0.01", message = "The amount must be greater than 0")
@Digits(integer = 10, fraction = 2, message = "The amount must have a maximum of 10 integers and 2 decimal places.")
@Column(name = "amount", nullable = false, precision = 12, scale = 2)
private BigDecimal amount;

@NotNull(message = "Currency is mandatory")
@Size(max = 3, message = "Currency must have 3 characters.")
private String currency;

@NotNull(message = "The payment method is mandatory")
@Column(name = "payment_method")
@Enumerated(EnumType.STRING)
private PaymentMethod paymentMethod;

@NotNull(message = "The payment status is mandatory")
@Column(name = "payment_status")
@Enumerated(EnumType.STRING)
private PaymentStatus paymentStatus;

@Size(max = 100, message = "The id of the transaction cannot exceed 100 characters")
@Column(name = "transaction_id")
private String transactionId;

@Size(max = 200, message = "The description cannot exceed 200 characters")
private String description;

@Column(name = "payment_date")
private LocalDate paymenDate;

@Column(name = "payment_time")
private LocalTime paymentTime;

@Size(max = 300, message = "The notes cannot exceed 300 characters")
private String notes;

@Size(max = 4, message = "Cannot exceed 4 numbers.")
@Column(name = "card_last_four")
private String cardLastFour;

@Column(name = "card_holder_name")
@Size(max = 30, message = "Name cannot exceed 30 characters")
private String cardHolderName;

@Column(name = "refund_amount", precision = 12, scale = 2)
private BigDecimal refundAmount;

@Column(name =  "refund_date")
private LocalDate refundDate;

@Column(name = "refund_reason")
@Size(max = 400, message = "Refound reason cannot exceed 400 characters")
private String refundReason;

@CreationTimestamp
@Column(name = "created_at")
private LocalDateTime createdAt;

@UpdateTimestamp
@Column(name = "updated_at")
private LocalDateTime updatedAt;

private LocalDateTime completedAt;

private String externalReference; //basicamente esto te va a indicar de donde viene el pago (MP,credito, paypal, etc).

private LocalDateTime processingStartedAt;

private LocalDateTime expiresAt;

private String cardToken;





}
