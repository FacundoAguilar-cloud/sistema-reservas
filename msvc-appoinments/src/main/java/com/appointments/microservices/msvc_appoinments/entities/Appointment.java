package com.appointments.microservices.msvc_appoinments.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "appointments")
public class Appointment {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@Column(name = "client_id", nullable = false)
private Long clientId;

@Column(name = "shop_id", nullable = false)
private Long shopId;

@Column(name = "barber_id", nullable = false)
private Long barberId;  //tener en cuenta si solo hay un barbero disponible puede ser null ya que siempre será la misma persona

@Column(name = "service_name", nullable = false)
private String serviceName;

@Column(name = "service_description", nullable = false)
private String serviceDescription;

@Column(name = "service_price", nullable = false)
private BigDecimal servicePrice;

@Column(name = "appointment_date", nullable = false)
private LocalDate appointmentDate;

@Column(name = "appointment_time", nullable = false)
private LocalTime appointmentTime;

@Column(name = "appointment_duration", nullable = false)
private Integer appointmentDuration;

@Column(name = "status", nullable = false)
@Enumerated(EnumType.STRING)
private AppointmentStatus status = AppointmentStatus.PENDING;

@Column(name = "client_notes", nullable = false)
private String clientNotes;

@Column(name = "barber_notes", nullable = false)
private String barberNotes; 

@Column(name = "cancellation_reason", nullable = false)
private String cancellationReason;

@Column(name = "cancellated_by", nullable = false)
private String cancellatedBy;

@Column(name = "cancelled_at", nullable = false)
private String cancelledAt;

@Column(name = "created_at", nullable = false)
private String createdAt;

@Column(name = "updated_at", nullable = false)
private String updatedAt;

// aca quizas deberiamos poner otra info importante que venga directamente de otros msvc(nombre, telefono, email, nombre de la tienda, etc)




}
