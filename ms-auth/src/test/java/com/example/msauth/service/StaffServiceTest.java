package com.example.msauth.service;

import com.example.msauth.dto.StaffDTO;
import com.example.msauth.model.Staff;
import com.example.msauth.repository.StaffRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffServiceTest {

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private KeycloakService keycloakService;

    @InjectMocks
    private StaffService staffService;

    private Staff staff;
    private StaffDTO staffDTO;

    @BeforeEach
    void setUp() {
        staff = new Staff();
        staff.setId(1L);
        staff.setFirstName("Alice");
        staff.setLastName("Smith");
        staff.setEmail("alice@example.com");
        staff.setRole("DOCTOR");
        staff.setKeycloakId("kc-123");

        staffDTO = new StaffDTO();
        staffDTO.setFirstName("Alice");
        staffDTO.setLastName("Smith");
        staffDTO.setEmail("alice@example.com");
        staffDTO.setRole("DOCTOR");
        staffDTO.setPassword("password123");
    }

    @Test
    void getAllStaff_ShouldReturnList() {
        when(staffRepository.findAll()).thenReturn(List.of(staff));

        List<StaffDTO> result = staffService.getAllStaff();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(staffRepository).findAll();
    }

    @Test
    void getStaffById_ShouldReturnStaff() {
        when(staffRepository.findById(1L)).thenReturn(Optional.of(staff));

        StaffDTO result = staffService.getStaffById(1L);

        assertNotNull(result);
        assertEquals("Alice", result.getFirstName());
    }

    @Test
    void getStaffById_ShouldThrowException_WhenNotFound() {
        when(staffRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> staffService.getStaffById(1L));
    }

    @Test
    void createStaff_ShouldSaveStaffAndCreateKeycloakUser() {
        when(keycloakService.createUser(any(), any(), any(), any(), any(), any())).thenReturn("kc-123");
        when(staffRepository.save(any(Staff.class))).thenReturn(staff);

        StaffDTO result = staffService.createStaff(staffDTO);

        assertNotNull(result);
        verify(keycloakService).createUser(any(), any(), any(), any(), any(), any());
        verify(staffRepository).save(any(Staff.class));
    }

    @Test
    void updateStaff_ShouldUpdateExistingStaff() {
        when(staffRepository.findById(1L)).thenReturn(Optional.of(staff));
        when(staffRepository.save(any(Staff.class))).thenReturn(staff);

        StaffDTO result = staffService.updateStaff(1L, staffDTO);

        assertNotNull(result);
        verify(keycloakService).updateUser(any(), any(), any(), any(), any());
        verify(staffRepository).save(any(Staff.class));
    }

    @Test
    void deleteStaff_ShouldDeleteFromLocalAndKeycloak() {
        when(staffRepository.findById(1L)).thenReturn(Optional.of(staff));

        staffService.deleteStaff(1L);

        verify(keycloakService).deleteUser("kc-123");
        verify(staffRepository).delete(staff);
    }
}
