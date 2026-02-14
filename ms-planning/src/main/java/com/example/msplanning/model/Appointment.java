package com.example.msplanning.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    private String staffId; // Keycloak ID of the staff who created the appointment

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Start time is required")
    @Future(message = "Appointment must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resource resource;

    private String type;
    private String status;
}
