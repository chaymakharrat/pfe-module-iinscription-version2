package com.example.workflow.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "department-service", url = "${services.department.url}")
public interface DepartmentServiceClient {

    @GetMapping("/api/departments/{id}/capacity")
    boolean checkCapacity(@PathVariable Long id);
}