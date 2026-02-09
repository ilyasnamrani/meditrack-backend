package com.example.msplanning.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class InvoiceVO {
    private Long patientId;
    private Long appointmentId;
    private BigDecimal amount;
    private String status;
}
