package com.example.msplanning.dto;

import lombok.Data;

@Data
public class StaffVO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
}
