package com.appointments.microservices.msvc_appoinments.request;

import com.appointments.microservices.msvc_appoinments.entities.AppointmentStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeAppointmentStatusRequest {

@NotNull(message = "Status is mandatory.")    
private AppointmentStatus status;

@Size(max = 700, message = "Notes cannot exceed 700 characters.")
private String notes;

@Size(max = 500, message = "Cancellation reason cannot exceed 500 characters.")
private String cancellationReason;

}
