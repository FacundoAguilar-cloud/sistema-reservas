package com.appointments.microservices.msvc_appoinments.servicies;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.appointments.microservices.msvc_appoinments.client.ShopClient;
import com.appointments.microservices.msvc_appoinments.client.UserClient;
import com.appointments.microservices.msvc_appoinments.config.AppointmentMapper;
import com.appointments.microservices.msvc_appoinments.dto.ShopDto;
import com.appointments.microservices.msvc_appoinments.dto.UserDto;
import com.appointments.microservices.msvc_appoinments.entities.Appointment;
import com.appointments.microservices.msvc_appoinments.entities.AppointmentStatus;
import com.appointments.microservices.msvc_appoinments.exceptions.BusinessException;
import com.appointments.microservices.msvc_appoinments.exceptions.ResourceNotFoundException;
import com.appointments.microservices.msvc_appoinments.exceptions.ServiceUnavailableException;
import com.appointments.microservices.msvc_appoinments.repository.AppointmentRepository;
import com.appointments.microservices.msvc_appoinments.request.AppointmentCreateRequest;
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



}



//aca vamos a necesitar algo que mapee la entidad a el dto response que nosotros utilizamos



