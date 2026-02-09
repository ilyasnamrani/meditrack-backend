package com.example.msplanning.controller;

import com.example.msplanning.dto.AppointmentDTO;
import com.example.msplanning.dto.ResourceDTO;
import com.example.msplanning.service.PlanningService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planning")
public class PlanningController {

    private final PlanningService planningService;

    public PlanningController(PlanningService planningService) {
        this.planningService = planningService;
    }

    @GetMapping("/resources")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'SECRETARY')")
    public ResponseEntity<List<ResourceDTO>> getAllResources() {
        return ResponseEntity.ok(planningService.getAllResources());
    }

    @PostMapping("/resources")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResourceDTO> createResource(@Valid @RequestBody ResourceDTO resourceDTO) {
        return ResponseEntity.ok(planningService.createResource(resourceDTO));
    }

    @PostMapping("/appointments")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'SECRETARY')")
    public ResponseEntity<AppointmentDTO> scheduleAppointment(@Valid @RequestBody AppointmentDTO appointmentDTO) {
        return ResponseEntity.ok(planningService.scheduleAppointment(appointmentDTO));
    }

    @PutMapping("/resources/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResourceDTO> updateResource(@PathVariable Long id,
                                                      @Valid @RequestBody ResourceDTO resourceDTO) {
        return ResponseEntity.ok(planningService.updateResource(id, resourceDTO));
    }

    @DeleteMapping("/resources/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        planningService.deleteResource(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/appointments/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'SECRETARY')")
    public ResponseEntity<AppointmentDTO> updateAppointment(@PathVariable Long id,
                                                            @Valid @RequestBody AppointmentDTO appointmentDTO) {
        return ResponseEntity.ok(planningService.updateAppointment(id, appointmentDTO));
    }

    @DeleteMapping("/appointments/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'SECRETARY')")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        planningService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
