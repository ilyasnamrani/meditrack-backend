package com.example.mspatient.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class PatientDTO {
    private Long id;
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Email(message = "Invalid email format")
    private String email;

    private String phoneNumber;

    private String registrationNumber;
    private List<MedicalRecordDTO> medicalRecords;
}
