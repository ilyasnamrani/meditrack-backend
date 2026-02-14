package com.example.msbilling.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    private Long appointmentId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private String status; // PENDING, PAID, CANCELLED
    private String staffId;

    private LocalDateTime issueDate;
    private LocalDateTime dueDate;
}
