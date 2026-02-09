package com.example.msplanning.dto;

import com.example.msplanning.model.ResourceType;
import lombok.Data;

@Data
public class ResourceDTO {
    private Long id;
    private String name;
    private ResourceType type;
    private boolean available;
}
