package com.appointments.microservices.msvc_appoinments.dto;

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
 private String openingTime;
 private String closingTime;
 private String status;
}
