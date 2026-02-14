package com.example.msalert.repository;

import com.example.msalert.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findAllByStaffId(String staffId);

    List<Alert> findAllByPatientId(Long patientId);

    List<Alert> findByReadFalse();
}
