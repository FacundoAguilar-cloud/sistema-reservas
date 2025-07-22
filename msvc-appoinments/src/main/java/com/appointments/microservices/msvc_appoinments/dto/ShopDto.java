package com.appointments.microservices.msvc_appoinments.dto;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//revisar si falta o hay que agregar mas cosas
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopDto {
 private Long id;
 private Long ownerId;
 private String name;
 private String address;
 private String phone;
 
 @JsonFormat(pattern = "HH:mm")
 private LocalTime openingTime;

 @JsonFormat(pattern = "HH:mm")
 private LocalTime closingTime;

 private String status;
}
