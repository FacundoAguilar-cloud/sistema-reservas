package com.appointments.microservices.msvc_appoinments.servicies;

import java.util.List;

import com.appointments.microservices.msvc_appoinments.response.AppointmentResponse;



public interface AppointmentServiceIMPL {
List <AppointmentResponse> findAll();

AppointmentResponse findById(Long appointmentId);





}
