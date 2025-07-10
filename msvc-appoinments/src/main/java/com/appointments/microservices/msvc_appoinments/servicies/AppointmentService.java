package com.appointments.microservices.msvc_appoinments.servicies;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.appointments.microservices.msvc_appoinments.client.ShopClient;
import com.appointments.microservices.msvc_appoinments.client.UserClient;
import com.appointments.microservices.msvc_appoinments.config.AppointmentMapper;
import com.appointments.microservices.msvc_appoinments.dto.ShopDto;
import com.appointments.microservices.msvc_appoinments.dto.UserDto;
import com.appointments.microservices.msvc_appoinments.entities.Appointment;
import com.appointments.microservices.msvc_appoinments.entities.AppointmentStatus;
import com.appointments.microservices.msvc_appoinments.exceptions.AppointmentException;
import com.appointments.microservices.msvc_appoinments.exceptions.BusinessException;
import com.appointments.microservices.msvc_appoinments.exceptions.ResourceNotFoundException;
import com.appointments.microservices.msvc_appoinments.exceptions.ServiceUnavailableException;
import com.appointments.microservices.msvc_appoinments.repository.AppointmentRepository;
import com.appointments.microservices.msvc_appoinments.request.AppointmentCreateRequest;
import com.appointments.microservices.msvc_appoinments.request.AppointmentUpdateRequest;
import com.appointments.microservices.msvc_appoinments.request.ChangeAppointmentStatusRequest;
import com.appointments.microservices.msvc_appoinments.response.AppointmentResponse;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

private final AppointmentRepository appointmentRepository;
private final AppointmentMapper appointmentMapper;
private final UserClient userClient;
private final ShopClient shopClient;

@Transactional
public AppointmentResponse createAppointment(AppointmentCreateRequest request, Long clientId){
   //Aca validamos que el usuario existe
   try {
      UserDto client = userClient.getUserById(clientId);
      if (client == null) {
         throw new ResourceNotFoundException("Client not found!");
      }
    } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Client not found.");
        } catch (FeignException e) {
            log.error("Error al validar cliente: {}", e.getMessage());
            throw new ServiceUnavailableException("User service not available");
        }
        //Aca vamos a validar la existencia de la tienda

   try {
      ShopDto shop = shopClient.getShopById(request.getShopId());
      if (shop == null) {
         throw new ResourceNotFoundException("Shop not found");
      }
      //ok validado
      validateShopOperatingHours(shop, request.getAppoitmentDate());
   } catch (FeignException.NotFound e) {
     throw new ResourceNotFoundException("Shop not found");
   } 
   catch(FeignException e){
      throw new ServiceUnavailableException("Shop service not available");
   } //validar conflicto de horarios
     validateAppointmentConflicts(request);
     
     //validar que la fecha no sea muy lejana (por ej, como maximo 1 mes)
      validateAppointmentDateRange(request.getAppoitmentDate());

      Appointment appointment = new Appointment();
      appointment.setClientId(clientId);
      appointment.setShopId(request.getShopId());
      appointment.setServiceName(request.getServiceName());
      appointment.setServiceDescription(request.getServiceDescription());
      appointment.setServicePrice(request.getServicePrice());
      appointment.setAppoitmentDate(request.getAppoitmentDate());
      appointment.setAppointmentDuration(request.getAppointmentDuration());
      appointment.setClientNotes(request.getClientNotes());
      appointment.setStatus(request.getStatus());

      Appointment savedAppointment = appointmentRepository.save(appointment);

      return appointmentMapper.toResponse(savedAppointment);

      }


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


public List <AppointmentResponse> getAppointmentsByStatus(AppointmentStatus status){
   List <Appointment> appointments = appointmentRepository.findAppointmentsByStatus(status);
   return appointments.stream()
   .map(appointmentMapper::toResponse)
   .toList();
}

public List <AppointmentResponse> getAppointmentsByDateRange(Long shopId, LocalDateTime startTime, LocalDateTime endTime){
    List <Appointment> appointments = appointmentRepository.findAppointmentsBetweenDates(shopId, startTime, endTime);
    return appointments.stream().map(appointmentMapper::toResponse).toList();
}

@Transactional
public AppointmentResponse updateAppointment(Long id, AppointmentUpdateRequest request, Long userId){
 Appointment appointment = appointmentRepository.findById(id)
 .orElseThrow(()-> new ResourceNotFoundException("Appointment not found"));

 //aca deberiamos tener metodos que verifiquen tanto los permisos que tiene el usuario y si el appointment puede ser modificado  
 validateUserPermissions(appointment, userId); 
 if (request.getBarberId() != null) {
   appointment.setBarberId(request.getBarberId());
 } 
 if (request.getServiceName() != null) {
   appointment.setServiceName(request.getServiceName());
 }
 if (request.getServiceDescription() != null) {
   appointment.setServiceDescription(request.getServiceDescription());
 }
 if (request.getServicePrice() != null) {
   appointment.setServicePrice(request.getServicePrice());
 }
 if (request.getAppoitmentDate() != null) {
   appointment.setAppoitmentDate(request.getAppoitmentDate());
   //aca habria que validar conflictos con la nueva fecha
   validateAppointmentDateRange(request.getAppoitmentDate());
   validateAppointmentConflictsForUpdate(appointment, request.getAppoitmentDate(), request.getAppointmentDuration());
 }
 if (request.getAppointmentDuration() != null) {
   appointment.setAppointmentDuration(request.getAppointmentDuration());
 }
 if (request.getClientNotes() != null) {
   appointment.setClientNotes(request.getClientNotes());
 }
 if (request.getBarberNotes() != null) {
   appointment.setBarberNotes(request.getBarberNotes());
 }
 
 Appointment updatedAppointment = appointmentRepository.save(appointment);

 return appointmentMapper.toResponse(updatedAppointment);
}

public AppointmentResponse changeAppointmentStatus(Long id, ChangeAppointmentStatusRequest request, Long userId){
 Appointment appointment = appointmentRepository.findById(id)
 .orElseThrow(() -> new ResourceNotFoundException("Appointment dont found, please try again."));
 //aca vamos a validar el usuario, no queremos que cualquiera pueda cambiar el status de la cita asi como si nada
 validateUserPermissions(appointment, userId);
 //tambien deberiamos validar el cambio de status
 validateStatusChange(appointment.getStatus(), request.getStatus());

 appointment.setStatus(request.getStatus());

 if (request.getNotes() != null) {
   if (appointment.getClientId().equals(userId)) {
      appointment.setClientNotes(request.getNotes());
   }
 }else{
   appointment.setBarberNotes(request.getNotes());
 }
 if (request.getStatus() == AppointmentStatus.CANCELED) {
   appointment.setCancellationReason(request.getCancellationReason());
   appointment.setCancellatedBy(request.getCancellationReason());
   appointment.setCancelledAt(LocalDateTime.now().toString());
 }
 Appointment updatedAppointment = appointmentRepository.save(appointment);

 return appointmentMapper.toResponse(updatedAppointment);
}

public void deleteAppointment(Long id, Long userId){
   Appointment appointment = appointmentRepository.findById(id)
   .orElseThrow(() -> new AppointmentException("Appointment dont found, please try again."));

   validateUserPermissions(appointment, userId);
   validateAppointmentDeleted(appointment);

   appointmentRepository.delete(appointment);
}




  private void validateShopOperatingHours(ShopDto shop, LocalDateTime appointmentDate){
   LocalTime appointmenTime = appointmentDate.toLocalTime();
   LocalTime openingTime = LocalTime.parse(shop.getOpeningTime());
   LocalTime closingTime = LocalTime.parse(shop.getClosingTime());

   if (appointmenTime.isBefore(openingTime) || appointmenTime.isAfter(closingTime)) {
      throw new BusinessException("The appointment is out of business hours.");
   }
  } 
  
  private void validateAppointmentConflicts(AppointmentCreateRequest request){
   List <Appointment> existingAppointments = appointmentRepository.findAppointmentsBetweenDates(
      request.getShopId(), 
      request.getAppoitmentDate(),
      request.getAppoitmentDate().plusMinutes(request.getAppointmentDuration()));

      if (existingAppointments.isEmpty()) {
         throw new BusinessException("There is already an appointment at that time.");
      }
  }

  private void validateAppointmentDateRange(LocalDateTime appointmentDate){
   LocalDateTime now = LocalDateTime.now();
   LocalDateTime maxDate = now.plusMonths(1);

   if (appointmentDate.isBefore(now)) {
      throw new BusinessException("Cannot create appointments in the past");
   }
   if (appointmentDate.isAfter(maxDate)) {
      throw new BusinessException("Cannot create appointments in the future.");
   }

  }

  private void validateUserPermissions(Appointment appointment, Long userId){
   if (appointment.getClientId().equals(userId)) {
      return;
   }
   ShopDto shop = shopClient.getShopById(appointment.getShopId());
   if (shop != null && shop.getOwnerId().equals(userId)) {
      return;
   }
   if (appointment.getBarberId() != null && appointment.getBarberId().equals(userId)) {
    return;  
   }

   throw new AppointmentException("You dont have permission to access this appointment.");

  }

  private void validateAppointmentConflictsForUpdate(Appointment appointment, LocalDateTime newDate, Integer newDuration ){
   LocalDateTime starTime = newDate;
   LocalDateTime endTime = starTime.plusMinutes(newDuration != null ? newDuration : appointment.getAppointmentDuration());

   List <Appointment> conflicts; 

   if (appointment.getBarberId() != null) {
      conflicts = appointmentRepository.findConflictsInAppointmentsForBarbers(appointment.getBarberId(), starTime, endTime);
   }
   else{
      conflicts = appointmentRepository.findConflictsInAppointmentsForShops(appointment.getShopId(), starTime, endTime);
   }
   //aca tendriamos que excluir la cita actual que tratamos de esa lista de conflictos

   conflicts = conflicts.stream().filter(conflict -> !conflict.getId().equals(appointment.getId())).collect(Collectors.toList());

   if (!conflicts.isEmpty()) {
      throw new AppointmentException("There is already an appointment at that time.");
   }

  } 

  private void validateStatusChange(AppointmentStatus actualStatus, AppointmentStatus newStatus){ //ok
   if (actualStatus == AppointmentStatus.COMPLETED && newStatus != AppointmentStatus.COMPLETED) {
      throw new AppointmentException("You cannot change the status of a completed appointment.");   
   }
  }

  private void validateAppointmentDeleted(Appointment appointment){
   if (appointment.getStatus() == AppointmentStatus.CONFIRMED || appointment.getStatus() == AppointmentStatus.IN_PROGRESS) {
      throw new AppointmentException("You cannot delete a completed or in-progress appointment.");
   }
  }

  private void validateAppointmentCanBeUpdated(Appointment appointment){
    if (appointment.getStatus() == AppointmentStatus.COMPLETED || appointment.getStatus() == AppointmentStatus.CANCELED) {
      throw new AppointmentException("You cannot update a completed or canceled appointment.");
   }
  }
  

  



}



//esto en teoria ya estaria terminado pero despues a la hora de probar los endopoints vamos a ver si funciona



