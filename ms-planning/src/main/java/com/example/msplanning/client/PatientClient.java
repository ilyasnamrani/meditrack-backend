package com.example.msplanning.client;

import com.example.msplanning.dto.PatientVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-patient")
public interface PatientClient {

    @GetMapping("/api/patients/{id}")
    PatientVO getPatientById(@PathVariable("id") Long id);
}
