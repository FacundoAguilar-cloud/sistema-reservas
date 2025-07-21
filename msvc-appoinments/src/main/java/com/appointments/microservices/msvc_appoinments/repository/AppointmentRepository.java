package com.appointments.microservices.msvc_appoinments.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

List <Appointment> findAppointmentByShopId(Long shopId);

List <Appointment> findAppointmentsByBarberId(Long barberId);

List <Appointment> findAppointmentsByStatus(AppointmentStatus status);

List <Appointment> findAppointmentsByClientIdAndStatus(Long clientId, AppointmentStatus status);

@Query("SELECT a FROM Appointment a WHERE a.clientId = :clientId AND a.appointmentDate BETWEEN :startDate AND :endDate")
List <Appointment> findAppointmentsBetweenDates(
@Param("clientId") Long clientId, 
@Param("startTime") LocalTime startTime, 
@Param("endTime") LocalTime endTime,
@Param ("appointmentDate") LocalDate appointmentDate); 


//otro meotodo que busque citas pero por barberia entre cierta fecha (no está en uso)

/*List <Appointment> findAppointmentsByShopBetweenDates(
    AppointmentStatus status,
    LocalDateTime startDate,
    LocalDateTime endDate);
*/

//otro metodo que evite posibles conflictos a la hora de reservar un turno con un barbero/estilista que ya está ocupado
@Query("SELECT a FROM Appointment a WHERE a.barberId = :barberId " +
"AND a.appointmentDate BETWEEN :startTime AND :endTime " +
"AND a.status IN ('PENDING', 'CONFIRMED')")
List <Appointment> findBarberAppointmentConflicts(
@Param("barberId") Long barberId,
@Param ("startTime") LocalDateTime startTime,
@Param ("endTime") LocalDateTime endTime   
);

@Query("SELECT a FROM Appointment a WHERE a.shopId = :shopId " +
"AND a.appointmentDate BETWEEN :startTime AND :endTime " +
"AND a.status != 'CANCELLED'")
List <Appointment> findAppointmentConflictsForShop(
@Param ("shopId") Long shopId,
@Param ("startTime") LocalDateTime starTime,
@Param ("endTime") LocalDateTime endTime    
);


@Query("SELECT a FROM Appointment a WHERE a.shopId = :shopId AND a.appointmentDate BETWEEN :startDate AND :endDate")
List<Appointment> findAppointmentsByShopAndDateRange(
    @Param("shopId") Long shopId,
    @Param("startDate") LocalDate startDate,
    @Param("endDate") LocalDate endDate
);







}


