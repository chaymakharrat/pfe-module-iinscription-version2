package pfe.example.enrollement_module.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import pfe.example.enrollement_module.model.*;


@FeignClient(name = "etudiant-service")
public interface EtudiantClient {
    @GetMapping("/api/etudiants/{id}")
    Etudiant getEtudiantById(@PathVariable("id") Long id);

    @GetMapping("/api/etudiants/matricule/{matricule}")
    Etudiant getEtudiantByMatricule(@PathVariable("matricule") String matricule);

    @GetMapping("/api/etudiants/email/{email}")
    Etudiant getEtudiantByEmail(@PathVariable("email") String email);

    @GetMapping("/api/etudiants/{id}/documents/check")
    Boolean hasAllRequiredDocuments(@PathVariable("id") Long id);
}
