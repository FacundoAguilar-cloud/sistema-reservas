package com.appointments.microservices.msvc_appoinments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//revisar si hace falta agregar mas cosas
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
 private Long id;
 private String name;
 private String email;
 private String phone;
 private String status;

}
