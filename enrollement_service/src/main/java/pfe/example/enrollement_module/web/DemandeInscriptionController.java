package pfe.example.enrollement_module.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pfe.example.enrollement_module.dto.HistoriqueRequest;
import pfe.example.enrollement_module.dto.StatusUpdateRequest;
import pfe.example.enrollement_module.entities.DemandeInscription;
import pfe.example.enrollement_module.services.DemandeInscriptionService;

import java.util.List;

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

    // 🆕 Pour Workflow: ajout historique
    @PostMapping("/enrollments/{id}/historique")
    public ResponseEntity<Void> addHistorique(
            @PathVariable Long id,
            @RequestBody HistoriqueRequest request
    ) {
        demandeInscriptionService.addHistoriqueFromWorkflow(id, request);
        return ResponseEntity.ok().build();
    }
    // 🆕 Mettre à jour le statut d'une demande
    @PutMapping("/api/enrollments/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request
    ) {
        demandeInscriptionService.updateStatus(id, request);
        return ResponseEntity.ok().build();
    }
    // 🆕 Endpoint pour récupérer toutes les demandes
    @GetMapping
    public ResponseEntity<List<DemandeInscription>> getAllDemandes() {
        return ResponseEntity.ok(demandeInscriptionService.getAllDemandes());
    }



}
