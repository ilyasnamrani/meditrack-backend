package com.example.msplanning.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlertVO {
    private String title;
    private String message;
    private String level; // INFO, WARNING, URGENT
    private Long targetUserId;
}
