package com.example.msauth.service;

import com.example.msauth.dto.StaffDTO;
import com.example.msauth.model.Staff;
import com.example.msauth.repository.StaffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StaffService {

    private final StaffRepository staffRepository;
    private final KeycloakService keycloakService;

    public StaffService(StaffRepository staffRepository, KeycloakService keycloakService) {
        this.staffRepository = staffRepository;
        this.keycloakService = keycloakService;
    }

    public List<StaffDTO> getAllStaff() {
        return staffRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public StaffDTO getStaffById(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Staff not found"));
        return mapToDTO(staff);
    }

    @Transactional
    public StaffDTO createStaff(StaffDTO staffDTO) {
        Staff staff = new Staff();
        staff.setFirstName(staffDTO.getFirstName());
        staff.setLastName(staffDTO.getLastName());
        staff.setEmail(staffDTO.getEmail());
        staff.setRole(staffDTO.getRole());

        // Create user in Keycloak
        try {
            String keycloakId = keycloakService.createUser(
                    staffDTO.getEmail(), // username
                    staffDTO.getEmail(),
                    staffDTO.getPassword(),
                    staffDTO.getFirstName(),
                    staffDTO.getLastName(),
                    staffDTO.getRole());
            staff.setKeycloakId(keycloakId);
        } catch (Exception e) {
            throw new RuntimeException("Error creating user in Keycloak: " + e.getMessage());
        }

        Staff savedStaff = staffRepository.save(staff);
        return mapToDTO(savedStaff);
    }

    @Transactional
    public StaffDTO updateStaff(Long id, StaffDTO staffDTO) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Staff not found"));

        staff.setFirstName(staffDTO.getFirstName());
        staff.setLastName(staffDTO.getLastName());
        // Email usually shouldn't change as it's the username, but if it does, it
        // requires Keycloak update
        // simple implementation assumes email/username is constant for now or handled
        // carefully
        staff.setRole(staffDTO.getRole());

        if (staff.getKeycloakId() != null) {
            try {
                keycloakService.updateUser(
                        staff.getKeycloakId(),
                        staff.getEmail(),
                        staffDTO.getFirstName(),
                        staffDTO.getLastName(),
                        staffDTO.getPassword() // Only updates if not null/empty
                );
            } catch (Exception e) {

                throw new RuntimeException("Error updating user in Keycloak: " + e.getMessage());
            }
        }

        Staff updatedStaff = staffRepository.save(staff);
        return mapToDTO(updatedStaff);
    }

    @Transactional
    public void deleteStaff(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Staff not found"));

        if (staff.getKeycloakId() != null) {
            try {
                keycloakService.deleteUser(staff.getKeycloakId());
            } catch (Exception e) {

                throw new RuntimeException("Error deleting user from Keycloak: " + e.getMessage());
            }
        }

        staffRepository.delete(staff);
    }

    private StaffDTO mapToDTO(Staff staff) {
        StaffDTO dto = new StaffDTO();
        dto.setId(staff.getId());
        dto.setFirstName(staff.getFirstName());
        dto.setLastName(staff.getLastName());
        dto.setEmail(staff.getEmail());
        dto.setRole(staff.getRole());
        return dto;
    }
}
