package com.appointments.microservices.msvc_appoinments.config;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

import com.appointments.microservices.msvc_appoinments.entities.Appointment;
import com.appointments.microservices.msvc_appoinments.request.AppointmentCreateRequest;
import com.appointments.microservices.msvc_appoinments.request.AppointmentUpdateRequest;
import com.appointments.microservices.msvc_appoinments.response.AppointmentResponse;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Component
public class AppointmentMapper {

public Appointment toEntity(AppointmentCreateRequest request){
    Appointment appointment = new Appointment();
    appointment.setServiceName(request.getServiceName());
    appointment.setServiceDescription(request.getServiceDescription());
    appointment.setServicePrice(request.getServicePrice());
    appointment.setAppointmentDate(request.getAppoitmentDate());
    appointment.setAppointmentDuration(request.getAppointmentDuration());
    appointment.setClientNotes(request.getClientNotes());
    appointment.setBarberNotes(request.getBarberNotes());
    return appointment;

}

public AppointmentResponse toResponse(Appointment appointment){
  AppointmentResponse appointmentResponse = new AppointmentResponse();
  appointmentResponse.setId(appointment.getId());
  appointmentResponse.setClientId(appointment.getClientId());
  appointmentResponse.setShopId(appointment.getShopId());
  appointmentResponse.setBarberId(appointment.getBarberId());
  appointmentResponse.setServiceName(appointment.getServiceName());
  appointmentResponse.setServiceDescription(appointment.getServiceDescription());
  appointmentResponse.setServicePrice(appointment.getServicePrice());
  appointmentResponse.setAppointmentDate(appointment.getAppointmentDate());
  appointmentResponse.setDurationMinutes(appointment.getAppointmentDuration());
  appointmentResponse.setClientNotes(appointment.getClientNotes());
  appointmentResponse.setBarberNotes(appointment.getBarberNotes());
  appointmentResponse.setStatus(appointment.getStatus());
  appointmentResponse.setCancellationReason(appointment.getCancellationReason());
  appointmentResponse.setCancelledAt(
    appointment.getCancelledAt() != null ? java.time.LocalDateTime.parse(appointment.getCancelledAt()) : null
  );
  appointmentResponse.setCancelledBy(
    appointment.getCancellatedBy() != null ? Long.parseLong(appointment.getCancellatedBy()) : null
  );
  return appointmentResponse;  
} 

public void updateEntiy(Appointment appointment, AppointmentUpdateRequest request){
 BeanUtils.copyProperties(request, appointment, getNullPropertyNames(request));
}
// copia del dto a la entidad unicamente los datos que NO son null (de todas formas hay que ver como funciona a la hora de utilizarlo)
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }

        return emptyNames.toArray(new String[0]);
    }


}
