package com.example.msauth;

import com.example.msauth.dto.StaffDTO;
import com.example.msauth.service.StaffService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class MsAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsAuthApplication.class, args);
    }

    @Bean
    CommandLineRunner initStaffData(StaffService staffService) {
        return args -> {

            try {
                // Staff 1: Administrator
                StaffDTO admin = new StaffDTO();
                admin.setFirstName("Alice");
                admin.setLastName("Martin");
                admin.setEmail("alice.martin@meditrack.com");
                admin.setRole("ADMIN");
                admin.setPassword("Admin@123");
                staffService.createStaff(admin);

                // Staff 2: Doctor
                StaffDTO doctor = new StaffDTO();
                doctor.setFirstName("Dr. Jean");
                doctor.setLastName("Dupont");
                doctor.setEmail("jean.dupont@meditrack.com");
                doctor.setRole("DOCTOR");
                doctor.setPassword("Doctor@123");
                staffService.createStaff(doctor);

                // Staff 3: Nurse
                StaffDTO nurse = new StaffDTO();
                nurse.setFirstName("Marie");
                nurse.setLastName("Leblanc");
                nurse.setEmail("marie.leblanc@meditrack.com");
                nurse.setRole("NURSE");
                nurse.setPassword("Nurse@123");
                staffService.createStaff(nurse);

            } catch (Exception e) {
            }
        };
    }
}
