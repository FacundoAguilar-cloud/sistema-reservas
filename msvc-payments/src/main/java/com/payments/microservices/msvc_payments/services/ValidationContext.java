package com.payments.microservices.msvc_payments.services;

import com.payments.microservices.msvc_payments.dto.AppointmentDto;
import com.payments.microservices.msvc_payments.dto.ShopDto;
import com.payments.microservices.msvc_payments.dto.UserDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidationContext {

private UserDto user;
private ShopDto shop;
private AppointmentDto appointment;


}
