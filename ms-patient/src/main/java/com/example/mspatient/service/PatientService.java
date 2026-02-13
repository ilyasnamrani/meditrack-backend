package com.example.mspatient.service;

import com.example.mspatient.dto.MedicalRecordDTO;
import com.example.mspatient.dto.PatientDTO;
import com.example.mspatient.model.MedicalRecord;
import com.example.mspatient.model.Patient;
import com.example.mspatient.repository.MedicalRecordRepository;
import com.example.mspatient.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final KeycloakService keycloakService;

    public PatientService(PatientRepository patientRepository,
            MedicalRecordRepository medicalRecordRepository,
            KeycloakService keycloakService) {
        this.patientRepository = patientRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.keycloakService = keycloakService;
    }

    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(this::mapToPatientDTO)
                .collect(Collectors.toList());
    }

    public PatientDTO getPatient(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Patient ID cannot be null");
        }
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
        return mapToPatientDTO(patient);
    }

    public PatientDTO findByRegistrationNumber(String registrationNumber) {
        return patientRepository.findByRegistrationNumber(registrationNumber)
                .map(this::mapToPatientDTO)
                .orElse(null);
    }

    @Transactional
    public PatientDTO createPatient(PatientDTO patientDTO) {
        Patient patient = new Patient();
        patient.setFirstName(patientDTO.getFirstName());
        patient.setLastName(patientDTO.getLastName());
        patient.setDateOfBirth(patientDTO.getDateOfBirth());
        patient.setEmail(patientDTO.getEmail());
        patient.setPhoneNumber(patientDTO.getPhoneNumber());

        // Generate Registration Number
        String registrationNumber = generateRegistrationNumber();
        patient.setRegistrationNumber(registrationNumber);

        // Create User in Keycloak
        // Default password or generated one. For simplicity, using "password" or
        // random.
        // In real app, send via email.
        // COMMENTED OUT: User requested no Keycloak account for patients, only local
        // DB.
        /*
         * // Generate random password
         * String tempPassword = java.util.UUID.randomUUID().toString().substring(0, 8);
         * System.out.println("GENERATED PASSWORD FOR PATIENT " + registrationNumber +
         * ": " + tempPassword);
         * try {
         * keycloakService.createPatientUser(registrationNumber, tempPassword,
         * patient.getFirstName(),
         * patient.getLastName(), patient.getEmail());
         * } catch (Exception e) {
         * throw new RuntimeException("Error creating patient user in Keycloak: " +
         * e.getMessage());
         * }
         */

        Patient savedPatient = patientRepository.save(patient);
        return mapToPatientDTO(savedPatient);
    }

    private String generateRegistrationNumber() {
        return "PAT-" + System.currentTimeMillis();
    }

    @Transactional
    public PatientDTO updatePatient(Long id, PatientDTO patientDTO) {
        if (id == null) {
            throw new IllegalArgumentException("Patient ID cannot be null");
        }
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        patient.setFirstName(patientDTO.getFirstName());
        patient.setLastName(patientDTO.getLastName());
        patient.setDateOfBirth(patientDTO.getDateOfBirth());
        patient.setEmail(patientDTO.getEmail());
        patient.setPhoneNumber(patientDTO.getPhoneNumber());

        Patient updatedPatient = patientRepository.save(patient);
        return mapToPatientDTO(updatedPatient);
    }

    public void deletePatient(Long id) {
        if (id == null || !patientRepository.existsById(id)) {
            throw new EntityNotFoundException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
    }

    @Transactional
    public MedicalRecordDTO addMedicalRecord(Long patientId, MedicalRecordDTO recordDTO) {
        if (patientId == null) {
            throw new IllegalArgumentException("Patient ID cannot be null");
        }
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        MedicalRecord record = new MedicalRecord();
        record.setPatient(patient);
        record.setVisitDate(recordDTO.getVisitDate() != null ? recordDTO.getVisitDate() : LocalDateTime.now());
        record.setDiagnosis(recordDTO.getDiagnosis());
        record.setTreatment(recordDTO.getTreatment());
        record.setPrescription(recordDTO.getPrescription());

        MedicalRecord savedRecord = medicalRecordRepository.save(record);
        return mapToRecordDTO(savedRecord);
    }

    private PatientDTO mapToPatientDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setDateOfBirth(patient.getDateOfBirth());
        dto.setEmail(patient.getEmail());
        dto.setPhoneNumber(patient.getPhoneNumber());
        dto.setRegistrationNumber(patient.getRegistrationNumber());
        if (patient.getMedicalRecords() != null) {
            dto.setMedicalRecords(patient.getMedicalRecords().stream()
                    .map(this::mapToRecordDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private MedicalRecordDTO mapToRecordDTO(MedicalRecord record) {
        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setId(record.getId());
        dto.setVisitDate(record.getVisitDate());
        dto.setDiagnosis(record.getDiagnosis());
        dto.setTreatment(record.getTreatment());
        dto.setPrescription(record.getPrescription());
        return dto;
    }
}
