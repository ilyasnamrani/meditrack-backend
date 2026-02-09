package com.example.msalert.repository;

import com.example.msalert.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertRepository  extends JpaRepository<Alert, Long> {
    List<Alert> findByTargetUserId(Long userId);
    List<Alert> findByReadFalse();
}
