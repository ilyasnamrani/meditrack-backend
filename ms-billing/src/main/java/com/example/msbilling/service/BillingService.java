package com.example.msbilling.service;

import com.example.msbilling.dto.InvoiceDTO;
import com.example.msbilling.model.Invoice;
import com.example.msbilling.model.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillingService {

    private final InvoiceRepository invoiceRepository;

    public BillingService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional
    public InvoiceDTO createInvoice(InvoiceDTO invoiceDTO) {
        Invoice invoice = new Invoice();
        invoice.setPatientId(invoiceDTO.getPatientId());
        invoice.setAppointmentId(invoiceDTO.getAppointmentId());
        invoice.setAmount(invoiceDTO.getAmount());
        invoice.setStatus("PENDING");
        invoice.setIssueDate(LocalDateTime.now());

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return mapToDTO(savedInvoice);
    }

    @Transactional
    public InvoiceDTO updateInvoice(Long id, InvoiceDTO invoiceDTO) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Invoice not found"));

        if (invoiceDTO.getAmount() != null) {
            invoice.setAmount(invoiceDTO.getAmount());
        }
        if (invoiceDTO.getStatus() != null) {
            invoice.setStatus(invoiceDTO.getStatus());
        }

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return mapToDTO(updatedInvoice);
    }

    public void deleteInvoice(Long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new jakarta.persistence.EntityNotFoundException("Invoice not found with id: " + id);
        }
        invoiceRepository.deleteById(id);
    }

    public List<InvoiceDTO> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public InvoiceDTO getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Invoice not found with id: " + id));
    }

    public List<InvoiceDTO> getInvoicesForPatient(Long patientId) {
        return invoiceRepository.findByPatientId(patientId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private InvoiceDTO mapToDTO(Invoice invoice) {
        return InvoiceDTO.builder()
                .id(invoice.getId())
                .patientId(invoice.getPatientId())
                .appointmentId(invoice.getAppointmentId())
                .amount(invoice.getAmount())
                .status(invoice.getStatus())
                .issueDate(invoice.getIssueDate())
                .build();
    }
}
