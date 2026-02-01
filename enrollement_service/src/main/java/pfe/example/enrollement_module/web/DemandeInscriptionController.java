package pfe.example.enrollement_module.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pfe.example.enrollement_module.entities.DemandeInscription;
import pfe.example.enrollement_module.services.DemandeInscriptionService;

@RestController
@RequestMapping("/api/demandes")
@RequiredArgsConstructor
public class DemandeInscriptionController {

    private final DemandeInscriptionService demandeInscriptionService;

    @PostMapping
    public ResponseEntity<DemandeInscription> submitCandidature(
            @RequestBody DemandeInscription request
    ) {
        return ResponseEntity.ok(
                demandeInscriptionService.submitCandidature(request)
        );
    }
}
