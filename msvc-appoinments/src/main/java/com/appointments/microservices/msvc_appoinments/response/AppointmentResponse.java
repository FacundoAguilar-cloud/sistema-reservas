package com.appointments.microservices.msvc_appoinments.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.appointments.microservices.msvc_appoinments.entities.AppointmentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private Long id;
    private Long clientId;
    private Long shopId;
    private Long barberId;
    private String serviceName;
    private String serviceDescription;
    private BigDecimal servicePrice;
    private LocalDateTime appointmentDate;
    private Integer durationMinutes;
    private AppointmentStatus status;
    private String clientNotes;
    private String barberNotes;
    private String cancellationReason;
    private Long cancelledBy;
    private LocalDateTime cancelledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt; //este por ahora va a quedar asi, despues vemos si agregamos otros datos como en la entidad principal con @Transient
}
