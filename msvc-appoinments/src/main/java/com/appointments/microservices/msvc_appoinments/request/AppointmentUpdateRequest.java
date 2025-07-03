package com.appointments.microservices.msvc_appoinments.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentUpdateRequest {

private Long shopId;

@Size(min = 2, max = 100, message = "Name of the service must be betweem 2 and 100 characters")
private String serviceName;

@Size(max = 500, message =  "The service description cannot exceed 500 characters")
private String serviceDescription;

@DecimalMin(value = "0.01", message = "Price must be greater than zero")
@Digits(integer = 8, fraction = 2, message = "Invalid price format!")
private BigDecimal servicePrice;

@Column(name = "appointment_date", nullable = false)
@Future(message = "Date must be future")
private LocalDateTime appoitmentDate;   

@Min(value = 15, message = "The minimun duration is 15 minutes")
@Max(value = 120, message = "The maximum duration is 120 minutes")
private Integer appointmentDuration;

@Size(max = 700, message = "Client notes cannot exceed 700 characters")
private String clientNotes;

@Size(max = 700, message = "Barber notes cannot exceed 700 characters")
private String barberNotes; 

}
