package com.payments.microservices.msvc_payments.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.netflix.discovery.converters.Converters.MetadataConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@Builder
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
@Column(name =  "card_holder_email")
@Size(max = 60, message = "card holder email cannot exceed 60 characters")
private String cardHolderEmail;

@Column(name = "card_holder_document_number")
private String cardHolderDocumentNumber;

@Column(name = "card_holder_document_type")
private String cardHolderDocumentType;

private String PaymentMethodId;

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

private String providerName;

private String providerPaymentUrl;

@Column(name = "idempotency_key", unique = true, length = 64)
private String idempotencyKey;

@Column(name = "client_ip", length = 45)
private String clientIp;

@Column(name = "user_agent", length = 255)
private String userAgent;

@Column(name = "processing_started_at")
private LocalDateTime processingStatedAt;

@Column(name = "expires_pending_payment_at")
private LocalDateTime expiresPendingPaymentAt;

@Column(name = "processingAttempts")
@Builder.Default
private Integer processingAttempts = 0;

@Column(name = "provider_transaction_id", length = 100)
private String providerTransactionId;

@Column(name = "metadata", columnDefinition = "TEXT")
@Convert(converter = MetadataConverter.class)
private Map<String, String> metadata = new HashMap<>();


  public Map<String, String> getMetadata() {
        if (metadata == null) {
            metadata = new HashMap<>();
        }
        return metadata;
    }





 public void markAsPaid(String transactionId) {
        this.paymentStatus = PaymentStatus.COMPLETED;
        this.transactionId = transactionId;
        this.completedAt = LocalDateTime.now();
    }
    
    public void markAsCancelled() {
        this.paymentStatus = PaymentStatus.CANCELLED;
        this.completedAt = LocalDateTime.now();
    }
    
    public void markAsRefunded() {
        this.paymentStatus = PaymentStatus.REFUNDED;
        this.refundDate = LocalDate.now();
    }

     public void incrementProcessingAttempts() {
        this.processingAttempts = (this.processingAttempts == null ? 0 : this.processingAttempts) + 1;
    }




}
