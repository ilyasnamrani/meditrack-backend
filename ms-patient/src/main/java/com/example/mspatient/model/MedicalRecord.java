package com.example.mspatient.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Visit date is required")
    private LocalDateTime visitDate;

    @NotBlank(message = "Diagnosis is required")
    private String diagnosis;

    private String treatment;

    private String prescription;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
}
