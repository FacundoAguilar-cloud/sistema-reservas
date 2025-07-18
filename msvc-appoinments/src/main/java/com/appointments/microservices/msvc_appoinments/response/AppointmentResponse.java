package com.appointments.microservices.msvc_appoinments.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.appointments.microservices.msvc_appoinments.entities.AppointmentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private Long id; //ok
    private Long clientId; //ok
    private Long shopId; //ok
    private Long barberId; //ok
    private String serviceName; //ok
    private String serviceDescription; //ok
    private BigDecimal servicePrice; //ok
    private LocalDate appointmentDate;
    private LocalTime appointmentTime; //ok
    private Integer durationMinutes; //ok
    private AppointmentStatus status; //ok
    private String clientNotes; //ok
    private String barberNotes; //ok
    private String cancellationReason; //ok
    private Long cancelledBy; //ok
    private LocalDateTime cancelledAt; //ok
    private LocalDateTime createdAt; //ok
    private LocalDateTime updatedAt; //este por ahora va a quedar asi, despues vemos si agregamos otros datos como en la entidad principal con @Transient
}
