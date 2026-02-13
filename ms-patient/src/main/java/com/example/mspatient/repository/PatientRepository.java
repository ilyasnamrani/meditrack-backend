package com.example.mspatient.repository;

import com.example.mspatient.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByRegistrationNumber(String registrationNumber);
}
