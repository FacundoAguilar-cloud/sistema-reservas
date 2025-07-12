package com.appointments.microservices.msvc_appoinments.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.appointments.microservices.msvc_appoinments.entities.Appointment;
import com.appointments.microservices.msvc_appoinments.entities.AppointmentStatus;

@Repository 
public interface AppointmentRepository extends CrudRepository <Appointment, Long> {

List <Appointment> findAppointmentsByClientId(Long clientId);

List <Appointment> findAppointmentByBarbershop(Long shopId);

List <Appointment> findAppointmentsByBarberId(Long barberId);

List <Appointment> findAppointmentsByStatus(AppointmentStatus status);

List <Appointment> findAppointmentsByClientIdAndStatus(Long clientId, AppointmentStatus status);

@Query("SELECT a FROM Appointment a WHERE a.userId = :userId AND a.dateTime BETWEEN :startDate AND :endDate")
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


//buscar citas que vienen en un futuro al dia en que se hace la consulta

List <Appointment> findUpcomingAppointments(
@Param ("startTime") LocalDateTime startTime,
@Param ("endTime") LocalDateTime endTime     
);










}


