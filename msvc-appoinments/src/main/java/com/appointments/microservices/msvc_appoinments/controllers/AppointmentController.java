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
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/appointment")
public class AppointmentController { 

private final AppointmentRepository appointmentRepository;
private final AppointmentServiceIMPL appointmentServiceIMPL;    

@GetMapping("/get-by-id")
@PreAuthorize("isAuthenticated()")
public ResponseEntity <AppointmentResponse> getAppointmentById(@RequestParam Long id, @RequestParam Long userId) {
    AppointmentResponse appointment = appointmentServiceIMPL.getAppointmentById(id, userId);

    return ResponseEntity.ok(appointment);

}

@GetMapping("/all")
public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
    List<AppointmentResponse> appointments = appointmentServiceIMPL.getAllAppointment();
    return ResponseEntity.ok(appointments);
}

@GetMapping("/by-client")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<List<AppointmentResponse>> getAppointmentsByClient(@PathVariable Long clientId) {
    List<AppointmentResponse> appointments = appointmentServiceIMPL.getAppointmentsByClient(clientId);
    return ResponseEntity.ok(appointments);
}

@GetMapping("/by-shop")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<List<AppointmentResponse>> GetAppointmentsByShop(@PathVariable Long shopId) {
    List<AppointmentResponse> appointments = appointmentServiceIMPL.getAppointmentsByShop(shopId);
    return ResponseEntity.ok(appointments);
}
@GetMapping("/by-barber")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<List<AppointmentResponse>> getAppointmentsByBarber(@PathVariable Long barberId) {
    List<AppointmentResponse>  appointments = appointmentServiceIMPL.getAppointmentsByBarber(barberId);
    return ResponseEntity.ok(appointments);
}

@GetMapping("/by-status")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<List<AppointmentResponse>> getAppointmentByStatus(@RequestParam AppointmentStatus status) {
    List<AppointmentResponse> appointments = appointmentServiceIMPL.getAppointmentsByStatus(status);
    return ResponseEntity.ok(appointments);
}

@GetMapping("/by-date-range")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<List<AppointmentResponse>> getAppointmentsByDateRange(
    @PathVariable Long shopId, 
    @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  LocalDate startDate,
    @RequestParam("endDate")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)   LocalDate endTime) {
    List <AppointmentResponse> appointments = appointmentServiceIMPL.getAppointmentsByDateRange(shopId, startDate, endTime);
    return ResponseEntity.ok(appointments);
}

@PostMapping("/create/{userId}")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<AppointmentResponse> createAppointment(
    @Valid @RequestBody AppointmentCreateRequest request,
    @PathVariable Long userId,
    HttpServletRequest httpRequest
    //faltaria el header para pasar el authorization
    ) {

     AppointmentResponse response = appointmentServiceIMPL.createAppointment(request, userId);
     return ResponseEntity.status(HttpStatus.CREATED).body(response);   
    
}

@PreAuthorize("hasAuthority('SHOP_OWNER')")
@PutMapping("update/{id}/{userId}")
//esto obviamente va a ser permitido unicamente si se tiene el rol necesario, hayq ue implementar toda la seguridad
public ResponseEntity<AppointmentResponse> updateAppointment(
    @PathVariable Long appointmentId, 
    @PathVariable Long userId,
    @Valid  @RequestBody AppointmentUpdateRequest request,
    @RequestHeader ("Authorization") String authHeader 
    // aca seguramente vamos a tener que pasar la autorizacion como header
    ) {
    
    appointmentServiceIMPL.updateAppointment(request, appointmentId, userId);    
    
    // You may want to return a proper ResponseEntity here, e.g., the updated appointment or a status
    return ResponseEntity.ok().build();
}

@PreAuthorize("hasAuthority('SHOP_OWNER')")
@DeleteMapping("/delete/{appointmentId}")
public ResponseEntity<AppointmentResponse> deleteAppointment(
    @PathVariable Long appointmentId,
    @PathVariable Long userId, //pasar la auth mediante header
    @RequestHeader ("Authorization") String authHeader 
){
    appointmentServiceIMPL.deleteAppointment(appointmentId, userId);

    return ResponseEntity.noContent().build();
}







}
