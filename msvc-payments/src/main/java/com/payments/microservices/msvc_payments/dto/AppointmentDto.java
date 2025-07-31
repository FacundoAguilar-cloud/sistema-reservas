package com.payments.microservices.msvc_payments.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDto {
  private Long id;
    
  private Long clientId;  
    
  private Long shopId;    
    
  private Long barberId;  
  
  private String serviceName;
    
  private BigDecimal servicePrice;
    
  private LocalDate appointmentDate;
    
  private LocalTime appointmentTime;
    
  private AppointmentStatus status;
}
