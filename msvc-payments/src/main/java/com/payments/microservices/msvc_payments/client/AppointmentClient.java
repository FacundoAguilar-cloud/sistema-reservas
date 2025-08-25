package com.payments.microservices.msvc_payments.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.payments.microservices.msvc_payments.dto.AppointmentDto;

@FeignClient(name = "msvc-appointments")
public interface AppointmentClient { 
@GetMapping("/api/appointment/get-by-id/{userId}")
AppointmentDto getAppointmentById(@PathVariable ("userId")Long id );

@PutMapping("update/{appointmentId}/{userId}")
ResponseEntity<Void> updateAppointmentStatus(@PathVariable Long appointmentId, @RequestParam String status); //despues hay que ver si anda (MIRAR)

}
