package pfe.example.enrollement_module.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import pfe.example.enrollement_module.model.DiplomeAEtudier;

@FeignClient(name = "departement-service")
public interface DepartementClient {
    @GetMapping("/api/departements/diplomes/nom/{nom}")
    DiplomeAEtudier getDiplomeByNom(@PathVariable("nom") String nom);

    @GetMapping("/api/departements/diplomes/{diplomeId}/can-accept-enrollment")
    Boolean canAcceptEnrollment(
            @PathVariable("diplomeId") Long diplomeId,
            @RequestParam("currentEnrollmentCount") int currentEnrollmentCount
    );

    @GetMapping("/api/departements/diplomes/nom/{nomDiplome}/frais")
    Double getFraisInscription(@PathVariable("nomDiplome") String nomDiplome);
}
