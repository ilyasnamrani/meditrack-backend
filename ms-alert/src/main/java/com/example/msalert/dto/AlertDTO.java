package com.example.msalert.dto;

import com.example.msalert.model.AlertLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AlertDTO {
    private Long id;
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    @NotNull(message = "Alert level is required")
    private AlertLevel level;

    private String staffId;
    private Long patientId;

    private String targetEmail;
    private LocalDateTime timestamp;
    private boolean read;
}
