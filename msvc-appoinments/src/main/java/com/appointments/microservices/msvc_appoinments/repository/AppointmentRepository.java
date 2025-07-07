package com.appointments.microservices.msvc_appoinments.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.appointments.microservices.msvc_appoinments.entities.Appointment;
import com.appointments.microservices.msvc_appoinments.entities.AppointmentStatus;

@Repository 
public interface AppointmentRepository extends CrudRepository <Appointment, Long> {

List <Appointment> findAppointmentsByClientId(Long clientId);

List <Appointment> findAppointmentByBarbershop(Long shopId);

List <Appointment> findAppointmentsByBarber(Long barberId);

List <Appointment> findAppointmentsByStatus(AppointmentStatus status);

List <Appointment> findAppointmentsByClientIdAndStatus(Long clientId, AppointmentStatus status);

//un método que busque reservas entre ciertas fechas
List <Appointment> findAppointmentsBetweenDates(
  Long shopId,
  LocalDateTime startDate,
  LocalDateTime endDate  
);

//otro meotodo que busque citas pero por barberia entre cierta fecha

List <Appointment> findAppointmentsByBarbershopBetweenDates(
    AppointmentStatus status,
    LocalDateTime startDate,
    LocalDateTime endDate);


//otro metodo que evite posibles conflictos a la hora de reservar un turno con un barbero/estilista que ya está ocupado
List <Appointment> findConflictsInAppointmentsForBarbers(
@Param("barberId") Long barberId,
@Param ("startTime") LocalDateTime startTime,
@Param ("endTime") LocalDateTime endTime   
);


List <Appointment> findConflictsInAppointmentsForShops(
@Param ("shopId") Long shopId,
@Param ("startTime") LocalDateTime starTime,
@Param ("endTime") LocalDateTime endTime    
);

//buscar citas pendientes que todavia requieren autorizacion

List <Appointment> findByPendingAppointments(@Param ("currentTime") LocalDateTime currentTime);


//buscar citas que vienen en un futuro al dia en que se hace la consulta

List <Appointment> findUpcomingAppointments(
@Param ("startTime") LocalDateTime startTime,
@Param ("endTime") LocalDateTime endTime     
);










}


