package com.payments.microservices.msvc_payments.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.payments.microservices.msvc_payments.dto.AppointmentDto;

@FeignClient(name = "msvc-appointments")
public interface AppointmentClient { 
@GetMapping("/api/appointment/get-by-id/{userId}")
AppointmentDto getAppointmentById(@PathVariable ("userId")Long id );

}
