package com.example.workflow.client;

import com.example.workflow.dto.DemandeInscriptionDTO;
import com.example.workflow.dto.StatusUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "enrollment-service", url = "${services.enrollment.url}")
public interface EnrollmentServiceClient {

    @PutMapping("api/demandes/api/enrollments/{id}/status")
    void updateStatus(@PathVariable("id") Long id, @RequestBody StatusUpdateRequest request);

    @PostMapping("/api/api/demandes/enrollments/{id}/historique")
    void addHistorique(@PathVariable("id") Long id, @RequestBody StatusUpdateRequest request);
    @GetMapping("/api/enrollments/{id}")
    DemandeInscriptionDTO getEnrollment(@PathVariable("id") Long id);
}