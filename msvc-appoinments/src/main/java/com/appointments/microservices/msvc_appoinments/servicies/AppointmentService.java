package com.appointments.microservices.msvc_appoinments.servicies;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.appointments.microservices.msvc_appoinments.config.AppointmentMapper;
import com.appointments.microservices.msvc_appoinments.entities.Appointment;
import com.appointments.microservices.msvc_appoinments.entities.AppointmentStatus;
import com.appointments.microservices.msvc_appoinments.exceptions.ResourceNotFoundException;
import com.appointments.microservices.msvc_appoinments.repository.AppointmentRepository;
import com.appointments.microservices.msvc_appoinments.request.AppointmentCreateRequest;
import com.appointments.microservices.msvc_appoinments.response.AppointmentResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

private final AppointmentRepository appointmentRepository;
private final AppointmentMapper appointmentMapper;


@Transactional(readOnly = true)    
public  AppointmentResponse getAppointmentById(Long id, Long userId){
    Appointment appointment = appointmentRepository.findById(id).
    orElseThrow(() -> new ResourceNotFoundException("Appointment not found please check the Id and try again."));
    return appointmentMapper.toResponse(appointment);
}

public List<AppointmentResponse> getAllAppointment() {
    List<Appointment> appointments = (List<Appointment>) appointmentRepository.findAll();
    return appointments.stream()
            .map(appointmentMapper::toResponse)
            .toList();
}

public List <AppointmentResponse> getAppointmentByClient(Long clientId){
   List <Appointment> appointments = appointmentRepository.findAppointmentsByClientId(clientId);
   return appointments.stream()
   .map(appointmentMapper::toResponse).
   toList();
}

public List <AppointmentResponse> getAppointmentsByShop(Long shopId){
   List <Appointment> appointments = appointmentRepository.findAppointmentByBarbershop(shopId);
   return appointments.stream()
   .map(appointmentMapper::toResponse).
   toList();
}

public List <AppointmentResponse> getAppointmentsByBarber(Long barberId){
   List <Appointment> appointments = appointmentRepository.findAppointmentsByBarber(barberId);
   return appointments.stream()
   .map(appointmentMapper::toResponse).
   toList();
}


public List <AppointmentResponse> getAppointmentsByStaus(AppointmentStatus status){
   List <Appointment> appointments = appointmentRepository.findAppointmentsByStatus(status);
   return appointments.stream()
   .map(appointmentMapper::toResponse)
   .toList();
}

public List <AppointmentResponse> getAppointmentsByDateRange(Long shopId, LocalDateTime startTime, LocalDateTime endTime){
    List <Appointment> appointments = appointmentRepository.findAppointmentsBetweenDates(shopId, startTime, endTime);
    return appointments.stream().map(appointmentMapper::toResponse).toList();
}

//public AppointmentResponse createAppointment(AppointmentCreateRequest request, Long clientId){
    //Acá deberiamos validar que el usuario realmente existe por lo que deberiamos utilizar un userClient
}






//aca vamos a necesitar algo que mapee la entidad a el dto response que nosotros utilizamos



