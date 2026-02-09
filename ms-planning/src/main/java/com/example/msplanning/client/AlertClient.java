package com.example.msplanning.client;

import com.example.msplanning.dto.AlertVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-alert")
public interface AlertClient {

    @PostMapping("/api/alerts")
    void createAlert(@RequestBody AlertVO alertVO);
}
