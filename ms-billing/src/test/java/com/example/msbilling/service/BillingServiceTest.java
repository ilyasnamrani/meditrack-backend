package com.example.msbilling.service;

import com.example.msbilling.dto.InvoiceDTO;
import com.example.msbilling.model.Invoice;
import com.example.msbilling.model.InvoiceRepository;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private BillingService billingService;

    private Invoice invoice;
    private InvoiceDTO invoiceDTO;

    @BeforeEach
    void setUp() {
        invoice = new Invoice();
        invoice.setId(1L);
        invoice.setPatientId(1L);
        invoice.setAmount(new BigDecimal("100.00"));
        invoice.setStaffId("staff123");

        invoiceDTO = InvoiceDTO.builder()
                .patientId(1L)
                .amount(new BigDecimal("100.00"))
                .build();
    }

    private void mockSecurityContext() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaim("sub")).thenReturn("staff123");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createInvoice_ShouldSaveInvoice() {
        mockSecurityContext();
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

        InvoiceDTO result = billingService.createInvoice(invoiceDTO);

        assertNotNull(result);
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void getInvoiceById_ShouldReturnInvoice() {
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        InvoiceDTO result = billingService.getInvoiceById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getInvoiceById_ShouldThrowException_WhenNotFound() {
        when(invoiceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> billingService.getInvoiceById(1L));
    }

    @Test
    void getAllInvoices_ShouldReturnList() {
        mockSecurityContext();
        when(invoiceRepository.findAllByStaffId("staff123")).thenReturn(List.of(invoice));

        List<InvoiceDTO> result = billingService.getAllInvoices();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void deleteInvoice_ShouldCallDelete() {
        when(invoiceRepository.existsById(1L)).thenReturn(true);

        billingService.deleteInvoice(1L);

        verify(invoiceRepository).deleteById(1L);
    }
}
