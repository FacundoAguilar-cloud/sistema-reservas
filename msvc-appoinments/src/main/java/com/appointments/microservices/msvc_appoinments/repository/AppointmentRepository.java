package com.appointments.microservices.msvc_appoinments.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.appointments.microservices.msvc_appoinments.entities.Appointment;

@Repository 
public interface AppointmentRepository extends CrudRepository <Appointment, Long> {

}
