package pfe.example.finance_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pfe.example.finance_service.DTO.DemandeInfoDTO;
import pfe.example.finance_service.DTO.StatusUpdateRequest;

@FeignClient(name = "INSCRIPTION-SERVICE")
public interface EnrollmentServiceClient {

    // GET /api/demandes/{id}
    @GetMapping("/api/demandes/{id}")
    DemandeInfoDTO getDemandeInfo(@PathVariable Long id);

    // PUT /api/enrollments/{id}/status  ← attention au mapping dans ton controller
    @PutMapping("/api/demandes/enrollments/{id}/status")
    void updateStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request
    );
}
