package com.appointments.microservices.msvc_appoinments.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appointments.microservices.msvc_appoinments.entities.AppointmentStatus;
import com.appointments.microservices.msvc_appoinments.repository.AppointmentRepository;
import com.appointments.microservices.msvc_appoinments.response.AppointmentResponse;
import com.appointments.microservices.msvc_appoinments.servicies.AppointmentServiceIMPL;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;










@RestController
@RequiredArgsConstructor
@RequestMapping("/api/appointment")
public class AppointmentController {

private final AppointmentRepository appointmentRepository;
private final AppointmentServiceIMPL appointmentServiceIMPL;    

@GetMapping("/get-by-id")
public ResponseEntity <AppointmentResponse> getAppointmentById(@PathVariable Long id) {
    AppointmentResponse appointment = appointmentServiceIMPL.findById(id);

    return ResponseEntity.ok(appointment);

}

@GetMapping("/all")
public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
    List<AppointmentResponse> appointments = appointmentServiceIMPL.findAll();
    return ResponseEntity.ok(appointments);
}

@GetMapping("/by-client")
public ResponseEntity<List<AppointmentResponse>> getAppointmentsByClient(@PathVariable Long clientId) {
    List<AppointmentResponse> appointments = appointmentServiceIMPL.getAppointmentsByClient(clientId);
    return ResponseEntity.ok(appointments);
}

@GetMapping("/by-shop")
public ResponseEntity<List<AppointmentResponse>> GetAppointmentsByShop(@PathVariable Long shopId) {
    List<AppointmentResponse> appointments = appointmentServiceIMPL.getAppointemntsByShop(shopId);
    return ResponseEntity.ok(appointments);
}
@GetMapping("/by-barber")
public ResponseEntity<List<AppointmentResponse>> getAppointmentsByBarber(@PathVariable Long barberId) {
    List<AppointmentResponse>  appointments = appointmentServiceIMPL.getAppointmentsByBarber(barberId);
    return ResponseEntity.ok(appointments);
}

@GetMapping("/by-status")
public ResponseEntity<List<AppointmentResponse>> getAppointmentByStatus(@RequestParam AppointmentStatus status) {
    List<AppointmentResponse> appointments = appointmentServiceIMPL.getAppointmentsByStatus(status);
    return ResponseEntity.ok(appointments);
}

@GetMapping("/by-date-range")
public ResponseEntity<List<AppointmentResponse>> getAppointmentsByDateRange(
    @PathVariable Long shopId, 
    @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  LocalDateTime startTime,
    @RequestParam("endTime")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)   LocalDateTime endTime) {
    List <AppointmentResponse> appointments = appointmentServiceIMPL.getAppointmentsByDateRange(shopId, startTime, endTime);
    return ResponseEntity.ok(appointments);
}








}
