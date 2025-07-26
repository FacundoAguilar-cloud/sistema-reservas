package com.appointments.microservices.msvc_appoinments.servicies;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.appointments.microservices.msvc_appoinments.dto.ShopDto;
import com.appointments.microservices.msvc_appoinments.entities.Appointment;
import com.appointments.microservices.msvc_appoinments.entities.AppointmentStatus;
import com.appointments.microservices.msvc_appoinments.request.AppointmentCreateRequest;
import com.appointments.microservices.msvc_appoinments.request.AppointmentUpdateRequest;
import com.appointments.microservices.msvc_appoinments.request.ChangeAppointmentStatusRequest;
import com.appointments.microservices.msvc_appoinments.response.AppointmentResponse;



public interface AppointmentServiceIMPL {
List <AppointmentResponse> getAllAppointment();

AppointmentResponse getAppointmentById(Long id, Long userId);

List <AppointmentResponse> getAppointmentsByClient(Long clientId);

List <AppointmentResponse> getAppointmentsByShop(Long shopId);

List <AppointmentResponse> getAppointmentsByBarber(Long barberId);

List <AppointmentResponse> getAppointmentsByStatus(AppointmentStatus status);

List <AppointmentResponse> getAppointmentsByDateRange(Long shopId, LocalDate startDate, LocalDate endDate);

AppointmentResponse createAppointment(AppointmentCreateRequest request, Long clientId);

AppointmentResponse updateAppointment(AppointmentUpdateRequest request, Long id, Long userId); 

AppointmentResponse changeAppointmentStatus(ChangeAppointmentStatusRequest request, Long id, Long userId);

void deleteAppointment(Long id, Long userId);

void validateShopOperatingHours(ShopDto shop, LocalTime appointmentTime);

void validateAppointmentConflicts(AppointmentCreateRequest request);
 
void validateAppointmentDateRange(LocalDate appointmentDate);

void validateUserPermissions(Appointment appointment, Long userId);

void validateAppointmentConflictsForUpdate(Appointment appointment, LocalDate newDate, LocalTime newTime,  Integer newDuration );





}
