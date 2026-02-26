package pfe.example.finance_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "DEPARTEMENT-SERVICE")
public interface DepartementServiceClient {

    @GetMapping("/api/diplomes/nom/{nomDiplome}/langue/{langue}/frais")
    double getFraisInscription(
            @PathVariable String nomDiplome,
            @PathVariable String langue
    );
}