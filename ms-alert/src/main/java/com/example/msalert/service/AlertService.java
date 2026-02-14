package com.example.msalert.service;

import com.example.msalert.client.AuthClient;
import com.example.msalert.dto.AlertDTO;
import com.example.msalert.model.Alert;
import com.example.msalert.repository.AlertRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final AuthClient authClient;

    public AlertService(AlertRepository alertRepository, AuthClient authClient) {
        this.alertRepository = alertRepository;
        this.authClient = authClient;
    }

    public List<AlertDTO> getAlertsForStaff() {
        String staffId = getCurrentUserId();
        return alertRepository.findAllByStaffId(staffId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AlertDTO> getAlertsForPatient(Long patientId) {
        return alertRepository.findAllByPatientId(patientId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AlertDTO> getAllAlerts() {
        String staffId = getCurrentUserId();
        return alertRepository.findAllByStaffId(staffId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AlertDTO getAlertById(Long id) {
        return alertRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Alert not found with id: " + id));
    }

    @Transactional
    public AlertDTO createAlert(AlertDTO dto) {
        Alert alert = new Alert();
        alert.setTitle(dto.getTitle());
        alert.setMessage(dto.getMessage());
        alert.setLevel(dto.getLevel());
        alert.setStaffId(getCurrentUserId() != null ? getCurrentUserId() : dto.getStaffId());
        alert.setPatientId(dto.getPatientId());
        alert.setTargetEmail(dto.getTargetEmail());

        alert.setTimestamp(LocalDateTime.now());

        // Bonus: Notify Logic (Sending email/SMS) would go here

        Alert savedAlert = alertRepository.save(alert);
        return mapToDTO(savedAlert);
    }

    @Transactional
    public AlertDTO updateAlert(Long id, AlertDTO alertDTO) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Alert not found"));

        alert.setTitle(alertDTO.getTitle());
        alert.setMessage(alertDTO.getMessage());
        alert.setLevel(alertDTO.getLevel());
        // Typically we don't update targetUserId/Email or Timestamp for an alert,
        // but it depends on requirements. For now, allowing basic content updates.

        Alert updatedAlert = alertRepository.save(alert);
        return mapToDTO(updatedAlert);
    }

    public void deleteAlert(Long id) {
        if (!alertRepository.existsById(id)) {
            throw new jakarta.persistence.EntityNotFoundException("Alert not found with id: " + id);
        }
        alertRepository.deleteById(id);
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaim("sub");
        }
        return null;
    }

    private AlertDTO mapToDTO(Alert alert) {
        AlertDTO dto = new AlertDTO();
        dto.setId(alert.getId());
        dto.setTitle(alert.getTitle());
        dto.setMessage(alert.getMessage());
        dto.setLevel(alert.getLevel());
        dto.setStaffId(alert.getStaffId());
        dto.setPatientId(alert.getPatientId());
        dto.setTargetEmail(alert.getTargetEmail());
        dto.setTimestamp(alert.getTimestamp());
        dto.setRead(alert.isRead());
        return dto;
    }
}
