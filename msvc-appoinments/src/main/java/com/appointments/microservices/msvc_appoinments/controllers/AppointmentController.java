package com.appointments.microservices.msvc_appoinments.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appointments.microservices.msvc_appoinments.entities.AppointmentStatus;
import com.appointments.microservices.msvc_appoinments.repository.AppointmentRepository;
import com.appointments.microservices.msvc_appoinments.request.AppointmentCreateRequest;
import com.appointments.microservices.msvc_appoinments.request.AppointmentUpdateRequest;
import com.appointments.microservices.msvc_appoinments.response.AppointmentResponse;
import com.appointments.microservices.msvc_appoinments.servicies.AppointmentServiceIMPL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;


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

@PostMapping("/create")
public ResponseEntity<AppointmentResponse> createAppointment(
    @Valid @RequestBody AppointmentCreateRequest request,
    @PathVariable Long userId,
    HttpServletRequest httpRequest
    //faltaria el header para pasar el authorization
    ) {

     AppointmentResponse response = appointmentServiceIMPL.createAppointment(request, userId);
     return ResponseEntity.status(HttpStatus.CREATED).body(response);   
    
}

@PutMapping("update/{id}/{userId}")
//esto obviamente va a ser permitido unicamente si se tiene el rol necesario, hayq ue implementar toda la seguridad
public ResponseEntity<AppointmentResponse> updateAppointment(
    @PathVariable Long appointmentId, 
    @PathVariable Long userId,
    @Valid  @RequestBody AppointmentUpdateRequest request
    // aca seguramente vamos a tener que pasar la autorizacion como header
    ) {
    
    appointmentServiceIMPL.updateAppointment(request, appointmentId, userId);    
    
    // You may want to return a proper ResponseEntity here, e.g., the updated appointment or a status
    return ResponseEntity.ok().build();
}

@DeleteMapping("/delete/{appointmentId}")
public ResponseEntity<AppointmentResponse> deleteAppointment(
    @PathVariable Long appointmentId,
    @PathVariable Long userId //pasar la auth mediante header
){
    appointmentServiceIMPL.deleteAppointment(appointmentId, userId);

    return ResponseEntity.noContent().build();
}







}
