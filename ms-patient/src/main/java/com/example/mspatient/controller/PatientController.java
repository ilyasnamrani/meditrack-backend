package com.example.mspatient.controller;

import com.example.mspatient.dto.MedicalRecordDTO;
import com.example.mspatient.dto.PatientDTO;
import com.example.mspatient.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('NURSE')")
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/{id}") // TODO SECURITY: Implement ownership check - patients should only access their
    // own data
    // Current implementation allows any PATIENT role to access any patient ID
    // Recommended: Add @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE') or
    // (hasRole('PATIENT') and @patientSecurityService.isOwner(#id))")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'PATIENT')")
    public ResponseEntity<PatientDTO> getPatient(@PathVariable Long id) {
        // WARNING: No ownership verification implemented yet
        return ResponseEntity.ok(patientService.getPatient(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'SECRETARY')")
    public ResponseEntity<PatientDTO> createPatient(@Valid @RequestBody PatientDTO patientDTO) {
        return ResponseEntity.ok(patientService.createPatient(patientDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'SECRETARY')")
    public ResponseEntity<PatientDTO> updatePatient(@PathVariable Long id, @Valid @RequestBody PatientDTO patientDTO) {
        return ResponseEntity.ok(patientService.updatePatient(id, patientDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/records")
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public ResponseEntity<MedicalRecordDTO> addMedicalRecord(@PathVariable Long id,
            @Valid @RequestBody MedicalRecordDTO recordDTO) {
        return ResponseEntity.ok(patientService.addMedicalRecord(id, recordDTO));
    }
}
