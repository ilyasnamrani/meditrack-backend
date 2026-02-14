package com.example.msplanning.repository;

import com.example.msplanning.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByResourceIdAndStartTimeBetween(Long resourceId, LocalDateTime start, LocalDateTime end);
}
