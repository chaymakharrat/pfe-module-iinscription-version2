package pfe.example.finance_service.web;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import pfe.example.finance_service.DTO.FormulairePreferencesPaiementDTO;
import pfe.example.finance_service.entities.Facture;
import pfe.example.finance_service.entities.Paiement;
import pfe.example.finance_service.entities.Remise;
import pfe.example.finance_service.enumerateur.ModePaiement;
import pfe.example.finance_service.repositories.RemiseRepository;
import pfe.example.finance_service.service.FactureService;

import java.util.List;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
@Slf4j
public class FactureController {

    private final FactureService factureService;
    private final RemiseRepository remiseRepository;

    // Agent finance génère la facture
    @PostMapping("/factures/generer/{enrollmentId}")
    public ResponseEntity<Facture> genererFacture(
            @PathVariable Long enrollmentId,
            @RequestHeader("X-Login") String loginAgent) {
        return ResponseEntity.ok(
                factureService.genererFacture(enrollmentId, loginAgent));
    }

    // Candidat soumet ses préférences via token
    @PostMapping("/formulaire/{token}")
    public ResponseEntity<Void> soumettrePreferences(
            @PathVariable String token,
            @RequestBody FormulairePreferencesPaiementDTO dto) {
        factureService.soumettrePreferences(token, dto);
        return ResponseEntity.ok().build();
    }

    // Récupérer remises disponibles
    @GetMapping("/remises")
    public ResponseEntity<List<Remise>> getRemisesDisponibles() {
        return ResponseEntity.ok(remiseRepository.findByActifTrue());
    }

    // Générer token formulaire
    @PostMapping("/formulaire/token/{enrollmentId}")
    public ResponseEntity<String> genererToken(@PathVariable Long enrollmentId) {
        return ResponseEntity.ok(factureService.genererTokenFormulaire(enrollmentId));
    }

    // Enregistrer un paiement
    @PostMapping("/paiements/echeance/{echeanceId}")
    public ResponseEntity<Paiement> enregistrerPaiement(
            @PathVariable Long echeanceId,
            @RequestParam double montant,
            @RequestParam ModePaiement mode) {
        return ResponseEntity.ok(
                factureService.enregistrerPaiement(echeanceId, montant, mode));
    }

    // Récupérer facture par enrollment
//    @GetMapping("/factures/enrollment/{enrollmentId}")
//    public ResponseEntity<Facture> getFactureByEnrollment(
//            @PathVariable Long enrollmentId) {
//        return ResponseEntity.ok(
//                factureService.getFactureByEnrollmentId(enrollmentId));
//    }
}
