package com.example.msbilling.model;

import com.example.msbilling.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByPatientId(Long patientId);

    List<Invoice> findAllByStaffId(String staffId);
}
