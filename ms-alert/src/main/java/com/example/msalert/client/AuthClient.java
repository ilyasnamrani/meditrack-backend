package com.example.msalert.client;

import com.example.msalert.client.AuthClient;
import com.example.msalert.dto.StaffVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-auth")
public interface AuthClient {

    @GetMapping("/api/staff/{id}")
    StaffVO getStaffById(@PathVariable("id") Long id);
}
