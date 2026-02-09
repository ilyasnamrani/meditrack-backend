package com.example.msplanning.client;

import com.example.msplanning.dto.StaffVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-auth")
public interface AuthClient {

    @GetMapping("/api/staff/{id}")
    StaffVO getStaffById(@PathVariable("id") Long id);
}
