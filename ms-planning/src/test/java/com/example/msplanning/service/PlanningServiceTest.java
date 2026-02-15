package com.example.msplanning.service;

import com.example.msplanning.client.AlertClient;
import com.example.msplanning.client.AuthClient;
import com.example.msplanning.client.BillingClient;
import com.example.msplanning.client.PatientClient;
import com.example.msplanning.dto.*;
import com.example.msplanning.model.Appointment;
import com.example.msplanning.model.Resource;
import com.example.msplanning.repository.AppointmentRepository;
import com.example.msplanning.repository.ResourceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanningServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientClient patientClient;

    @Mock
    private AuthClient authClient;

    @Mock
    private AlertClient alertClient;

    @Mock
    private BillingClient billingClient;

    @InjectMocks
    private PlanningService planningService;

    private Resource resource;
    private Appointment appointment;
    private AppointmentDTO appointmentDTO;

    @BeforeEach
    void setUp() {
        resource = new Resource();
        resource.setId(1L);
        resource.setName("Consultation Room A");
        resource.setAvailable(true);

        appointment = new Appointment();
        appointment.setId(1L);
        appointment.setResource(resource);
        appointment.setPatientId(1L);
        appointment.setDoctorId(1L);
        appointment.setStartTime(LocalDateTime.now().plusDays(1));
        appointment.setEndTime(LocalDateTime.now().plusDays(1).plusHours(1));

        appointmentDTO = new AppointmentDTO();
        appointmentDTO.setResourceId(1L);
        appointmentDTO.setPatientId(1L);
        appointmentDTO.setDoctorId(1L);
        appointmentDTO.setStartTime(appointment.getStartTime());
        appointmentDTO.setEndTime(appointment.getEndTime());
    }

    @Test
    void getAllResources_ShouldReturnList() {
        when(resourceRepository.findAll()).thenReturn(List.of(resource));

        List<ResourceDTO> result = planningService.getAllResources();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(resourceRepository).findAll();
    }

    @Test
    void scheduleAppointment_ShouldSaveAppointment() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        when(patientClient.getPatientById(1L)).thenReturn(new PatientVO());

        StaffVO doctor = new StaffVO();
        doctor.setRole("DOCTOR");
        when(authClient.getStaffById(1L)).thenReturn(doctor);

        when(appointmentRepository.findByResourceIdAndStartTimeBetween(anyLong(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        AppointmentDTO result = planningService.scheduleAppointment(appointmentDTO);

        assertNotNull(result);
        verify(appointmentRepository).save(any(Appointment.class));
        verify(alertClient).createAlert(any());
        verify(billingClient).createInvoice(any());
    }

    @Test
    void scheduleAppointment_ShouldThrowException_WhenResourceUnavailable() {
        resource.setAvailable(false);
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));

        assertThrows(IllegalStateException.class, () -> planningService.scheduleAppointment(appointmentDTO));
    }

    @Test
    void scheduleAppointment_ShouldThrowException_WhenConflictsExist() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        when(patientClient.getPatientById(1L)).thenReturn(new PatientVO());

        StaffVO doctor = new StaffVO();
        doctor.setRole("DOCTOR");
        when(authClient.getStaffById(1L)).thenReturn(doctor);

        when(appointmentRepository.findByResourceIdAndStartTimeBetween(anyLong(), any(), any()))
                .thenReturn(List.of(new Appointment()));

        assertThrows(IllegalStateException.class, () -> planningService.scheduleAppointment(appointmentDTO));
    }

    @Test
    void deleteAppointment_ShouldCallDelete() {
        when(appointmentRepository.existsById(1L)).thenReturn(true);

        planningService.deleteAppointment(1L);

        verify(appointmentRepository).deleteById(1L);
    }
}
