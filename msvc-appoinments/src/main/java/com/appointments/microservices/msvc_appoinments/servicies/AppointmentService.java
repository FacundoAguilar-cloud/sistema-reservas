package com.appointments.microservices.msvc_appoinments.servicies;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.appointments.microservices.msvc_appoinments.client.ShopClient;
import com.appointments.microservices.msvc_appoinments.client.UserClient;
import com.appointments.microservices.msvc_appoinments.config.AppointmentMapper;
import com.appointments.microservices.msvc_appoinments.config.ShopMapper;
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
public class AppointmentService implements AppointmentServiceIMPL {

private final AppointmentRepository appointmentRepository;
private final AppointmentMapper appointmentMapper;
private final UserClient userClient;
private final ShopClient shopClient;
private final ShopMapper shopMapper;

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
     Map<String, Object> shopData = shopClient.getShopById(request.getShopId());
      if (shopData == null) {
         throw new ResourceNotFoundException("Shop not found");
      }
      //aca vamos a debuggear para ver si encontramos el error
      System.out.println("=== APPOINTMENT DATA DEBUG ===");
      System.out.println("Appointment Date: " + request.getAppointmentDate());
      System.out.println("Appointment Time: " + request.getAppointmentTime());
      System.out.println("Appointment duration" + request.getAppointmentDuration());
      System.out.println("Shop opening time: " + shopData.get("openingTime"));
      System.out.println("Shop closing time: " + shopData.get("closingTime"));
      if (request.getAppointmentTime() != null) {
       
         ShopDto shop = shopMapper.mapToShopDto(shopData);
         validateShopOperatingHours(shop, request.getAppointmentTime());
      }
      

   } catch (FeignException.NotFound e) {
     throw new ResourceNotFoundException("Shop not found");
   } 
   catch(FeignException e){
      throw new ServiceUnavailableException("Shop service not available");
   } //validar conflicto de horarios
     validateAppointmentConflicts(request);
     
     //validar que la fecha no sea muy lejana (por ej, como maximo 1 mes)
      validateAppointmentDateRange(request.getAppointmentDate());

      Appointment appointment = new Appointment();
      appointment.setClientId(clientId);
      appointment.setShopId(request.getShopId());
      appointment.setBarberId(request.getBarberId());
      appointment.setServiceName(request.getServiceName());
      appointment.setServiceDescription(request.getServiceDescription());
      appointment.setServicePrice(request.getServicePrice());
      appointment.setAppointmentDate(request.getAppointmentDate());
      appointment.setAppointmentTime(request.getAppointmentTime());
      appointment.setAppointmentDuration(request.getAppointmentDuration());
      appointment.setClientNotes(request.getClientNotes());
      appointment.setBarberNotes(request.getBarberNotes());
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

public List <AppointmentResponse> getAppointmentsByClient(Long clientId){
   List <Appointment> appointments = appointmentRepository.findAppointmentsByClientId(clientId);
   return appointments.stream()
   .map(appointmentMapper::toResponse).
   toList();
}

public List <AppointmentResponse> getAppointmentsByShop(Long shopId){
   List <Appointment> appointments = appointmentRepository.findAppointmentByShopId(shopId);
   return appointments.stream()
   .map(appointmentMapper::toResponse).
   toList();
}

public List <AppointmentResponse> getAppointmentsByBarber(Long barberId){
   List <Appointment> appointments = appointmentRepository.findAppointmentsByBarberId(barberId);
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

public List <AppointmentResponse> getAppointmentsByDateRange(Long shopId, LocalDate startDate, LocalDate endDate){
    List <Appointment> appointments = appointmentRepository.findAppointmentsByShopAndDateRange(shopId, startDate, endDate);
    return appointments.stream().map(appointmentMapper::toResponse).toList();
}

@Transactional
public AppointmentResponse updateAppointment(AppointmentUpdateRequest request, Long id , Long userId){
 Appointment appointment = appointmentRepository.findById(id)
 .orElseThrow(()-> new ResourceNotFoundException("Appointment not found"));

 //aca deberiamos tener metodos que verifiquen tanto los permisos que tiene el usuario y si el appointment puede ser modificado  
 validateUserPermissions(appointment, userId); 
 if (request.getShopId() != null) {
   appointment.setShopId(request.getShopId());
 }
 
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
 if (request.getAppointmentDate() != null) {
   appointment.setAppointmentDate(request.getAppointmentDate());
   //aca habria que validar conflictos con la nueva fecha
   validateAppointmentDateRange(request.getAppointmentDate());
   validateAppointmentConflictsForUpdate(appointment, request.getAppointmentDate(), request.getAppointmentDuration());
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

public AppointmentResponse changeAppointmentStatus(ChangeAppointmentStatusRequest request,Long id, Long userId){
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




  public void validateShopOperatingHours(ShopDto shop, LocalTime appointmentTime){
   //metemos un poco de debug para ver donde esta el error exactamente 
   System.out.println("=== VALIDATING SHOP HOURS ===");
    System.out.println("Shop opening time: " + shop.getOpeningTime());
    System.out.println("Shop closing time: " + shop.getClosingTime());
    LocalTime appointmenTime = appointmentTime;
    System.out.println("Appointment time: " + appointmenTime);
    //deberiamos tambien validar que los horarios no sean nulos o esten vacios
    if (shop.getOpeningTime() == null) {
        System.out.println("Warning: Shop opening time not configured, skipping validation.");
        return;
    }
    if (shop.getClosingTime() == null) {
        System.out.println("Warning: Shop closing time not configured, skipping validation.");
        return;
    }
    try {
    LocalTime openingTime = shop.getOpeningTime();
    LocalTime closingTime = shop.getClosingTime();

    if (appointmenTime.isBefore(openingTime) || appointmenTime.isAfter(closingTime)) {
      throw new BusinessException("The appointment is out of business hours.");
   }
    } catch (Exception e) {
      System.out.println("Error parsing shop hours ");
      throw new IllegalArgumentException("Invalid shop operating hours format"); 
    }
  } 
  
  public void validateAppointmentConflicts(AppointmentCreateRequest request){
   
   List <Appointment> existingAppointments = appointmentRepository.findAppointmentsBetweenDates(
      request.getClientId(), 
      request.getAppointmentDate(),
      request.getAppointmentTime(), // start time
      request.getAppointmentTime().plusMinutes(request.getAppointmentDuration()) // end time
   );
     
   if (!existingAppointments.isEmpty()) {
      throw new BusinessException("There is already an appointment at that time.");
   }
  }

  public void validateAppointmentDateRange(LocalDate appointmentDate){
   LocalDate now = LocalDate.now();
   LocalDate maxDate = now.plusMonths(1);

   if (appointmentDate.isBefore(now)) {
      throw new BusinessException("Cannot create appointments in the past");
   }
   if (appointmentDate.isAfter(maxDate)) {
      throw new BusinessException("Cannot create appointments in the future.");
   }

  }

  public void validateUserPermissions(Appointment appointment, Long userId){
   if (appointment.getClientId().equals(userId)) {
      return;
   }
   Map<String, Object> shopData = shopClient.getShopById(appointment.getShopId());
   ShopDto shop = shopMapper.mapToShopDto(shopData);
   if (shop != null && shop.getOwnerId().equals(userId)) {
      return;
   }
   if (appointment.getBarberId() != null && appointment.getBarberId().equals(userId)) {
    return;  
   }

   throw new AppointmentException("You dont have permission to access this appointment.");

  }

  public void validateAppointmentConflictsForUpdate(Appointment appointment, LocalDate newDate, Integer newDuration ){
   LocalDate starTime = newDate.atStartOfDay();
   LocalDate endTime = starTime.plusMinutes(newDuration != null ? newDuration : appointment.getAppointmentDuration());

   List <Appointment> conflicts; 

   if (appointment.getBarberId() != null) {
      conflicts = appointmentRepository.findBarberAppointmentConflicts(appointment.getBarberId(), starTime, endTime);
   }
   else{
      conflicts = appointmentRepository.findAppointmentConflictsForShop(appointment.getShopId(), starTime, endTime);
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



