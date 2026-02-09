package com.example.mspatient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MedicalRecordDTO {
    private Long id;
    @NotNull(message = "Visit date is required")
    private LocalDateTime visitDate;

    @NotBlank(message = "Diagnosis is required")
    private String diagnosis;

    private String treatment;
    private String prescription;
}
