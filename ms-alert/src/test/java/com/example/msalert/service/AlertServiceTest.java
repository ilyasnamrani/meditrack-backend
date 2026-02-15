package com.example.msalert.service;

import com.example.msalert.client.AuthClient;
import com.example.msalert.dto.AlertDTO;
import com.example.msalert.model.Alert;
import com.example.msalert.repository.AlertRepository;
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
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    @SuppressWarnings("unused")
    private AuthClient authClient;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private AlertService alertService;

    private Alert alert;
    private AlertDTO alertDTO;

    @BeforeEach
    void setUp() {
        alert = new Alert();
        alert.setId(1L);
        alert.setTitle("High Heart Rate");
        alert.setMessage("Heart rate exceeded 100 bpm");
        alert.setStaffId("staff123");
        alert.setPatientId(1L);

        alertDTO = new AlertDTO();
        alertDTO.setTitle("High Heart Rate");
        alertDTO.setMessage("Heart rate exceeded 100 bpm");
        alertDTO.setPatientId(1L);
    }

    private void mockSecurityContext() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaim("sub")).thenReturn("staff123");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getAlertsForStaff_ShouldReturnList() {
        mockSecurityContext();
        when(alertRepository.findAllByStaffId("staff123")).thenReturn(List.of(alert));

        List<AlertDTO> result = alertService.getAlertsForStaff();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(alertRepository).findAllByStaffId("staff123");
    }

    @Test
    void createAlert_ShouldSaveAlert() {
        mockSecurityContext();
        when(alertRepository.save(any(Alert.class))).thenReturn(alert);

        AlertDTO result = alertService.createAlert(alertDTO);

        assertNotNull(result);
        assertEquals("High Heart Rate", result.getTitle());
        verify(alertRepository).save(any(Alert.class));
    }

    @Test
    void getAlertById_ShouldReturnAlert() {
        when(alertRepository.findById(1L)).thenReturn(Optional.of(alert));

        AlertDTO result = alertService.getAlertById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getAlertById_ShouldThrowException_WhenNotFound() {
        when(alertRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> alertService.getAlertById(1L));
    }

    @Test
    void deleteAlert_ShouldCallDelete() {
        when(alertRepository.existsById(1L)).thenReturn(true);

        alertService.deleteAlert(1L);

        verify(alertRepository).deleteById(1L);
    }
}
