package com.example.msplanning;

import com.example.msplanning.dto.AppointmentDTO;
import com.example.msplanning.model.Resource;
import com.example.msplanning.model.ResourceType;
import com.example.msplanning.repository.ResourceRepository;
import com.example.msplanning.service.PlanningService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class MsPlanningApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsPlanningApplication.class, args);
    }

    @Bean
    CommandLineRunner initPlanningData(PlanningService planningService, ResourceRepository resourceRepository) {
        return args -> {
            try {
                // Create Resources first
                Resource room1 = new Resource();
                room1.setName("Salle de Consultation 1");
                room1.setType(ResourceType.ROOM);
                room1.setAvailable(true);
                Resource savedRoom1 = resourceRepository.save(room1);

                Resource room2 = new Resource();
                room2.setName("Salle de Consultation 2");
                room2.setType(ResourceType.ROOM);
                room2.setAvailable(true);
                Resource savedRoom2 = resourceRepository.save(room2);

                Resource room3 = new Resource();
                room3.setName("Salle d'Examen");
                room3.setType(ResourceType.ROOM);
                room3.setAvailable(true);
                Resource savedRoom3 = resourceRepository.save(room3);

                // Wait a moment to ensure ms-auth and ms-patient are ready
                Thread.sleep(2000);

                // Create Appointments
                AppointmentDTO appointment1 = new AppointmentDTO();
                appointment1.setDoctorId(2L); // Dr. Jean Dupont from ms-auth
                appointment1.setPatientId(1L); // Sophie Bernard from ms-patient
                appointment1.setStartTime(LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0));
                appointment1.setEndTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0));
                appointment1.setResourceId(savedRoom1.getId());
                planningService.scheduleAppointment(appointment1);

                AppointmentDTO appointment2 = new AppointmentDTO();
                appointment2.setDoctorId(2L); // Dr. Jean Dupont
                appointment2.setPatientId(2L); // Thomas Rousseau from ms-patient
                appointment2.setStartTime(LocalDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0));
                appointment2.setEndTime(LocalDateTime.now().plusDays(1).withHour(15).withMinute(0).withSecond(0));
                appointment2.setResourceId(savedRoom2.getId());
                planningService.scheduleAppointment(appointment2);

                AppointmentDTO appointment3 = new AppointmentDTO();
                appointment3.setDoctorId(2L); // Dr. Jean Dupont
                appointment3.setPatientId(3L); // Emma Petit from ms-patient
                appointment3.setStartTime(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0));
                appointment3.setEndTime(LocalDateTime.now().plusDays(2).withHour(11).withMinute(0).withSecond(0));
                appointment3.setResourceId(savedRoom3.getId());
                planningService.scheduleAppointment(appointment3);

            } catch (Exception e) {
                // Silently handle
            }
        };
    }
}
