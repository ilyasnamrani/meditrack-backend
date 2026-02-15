package com.example.mspatient.service;

import com.example.mspatient.dto.MedicalRecordDTO;
import com.example.mspatient.dto.PatientDTO;
import com.example.mspatient.model.MedicalRecord;
import com.example.mspatient.model.Patient;
import com.example.mspatient.repository.MedicalRecordRepository;
import com.example.mspatient.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    @SuppressWarnings("unused")
    private KeycloakService keycloakService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;
    private PatientDTO patientDTO;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setStaffId("staff123");

        patientDTO = new PatientDTO();
        patientDTO.setFirstName("John");
        patientDTO.setLastName("Doe");
        patientDTO.setEmail("john.doe@example.com");
    }

    private void mockSecurityContext() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaim("sub")).thenReturn("staff123");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getAllPatients_ShouldReturnList() {
        mockSecurityContext();
        List<Patient> patients = List.of(patient);
        when(patientRepository.findAllByStaffId("staff123")).thenReturn(patients);

        List<PatientDTO> result = patientService.getAllPatients();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        verify(patientRepository, times(1)).findAllByStaffId("staff123");
    }

    @Test
    void getPatient_ShouldReturnPatient() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        PatientDTO result = patientService.getPatient(1L);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    void getPatient_ShouldThrowException_WhenNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> patientService.getPatient(1L));
    }

    @Test
    void createPatient_ShouldSavePatient() {
        mockSecurityContext();
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        PatientDTO result = patientService.createPatient(patientDTO);

        assertNotNull(result);
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void updatePatient_ShouldUpdateExistingPatient() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        PatientDTO updatedDTO = new PatientDTO();
        updatedDTO.setFirstName("Jane");
        updatedDTO.setLastName("Doe");

        PatientDTO result = patientService.updatePatient(1L, updatedDTO);

        assertEquals("Jane", result.getFirstName());

        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void deletePatient_ShouldCallDelete() {
        when(patientRepository.existsById(1L)).thenReturn(true);

        patientService.deletePatient(1L);

        verify(patientRepository, times(1)).deleteById(1L);
    }

    @Test
    void addMedicalRecord_ShouldSaveRecord() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        MedicalRecord record = new MedicalRecord();
        record.setId(1L);
        record.setPatient(patient);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(record);

        MedicalRecordDTO recordDTO = new MedicalRecordDTO();
        recordDTO.setDiagnosis("Flu");

        MedicalRecordDTO result = patientService.addMedicalRecord(1L, recordDTO);

        assertNotNull(result);
        verify(medicalRecordRepository, times(1)).save(any(MedicalRecord.class));
    }
}
