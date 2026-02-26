// FormulairePreferencesController.java
package pfe.example.finance_service.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pfe.example.finance_service.DTO.FormulairePreferencesPaiementDTO;
import pfe.example.finance_service.service.FormulairePreferencesService;
import pfe.example.finance_service.service.PreferencesRequest;

@RestController
@RequestMapping("/api/formulaire-preferences")
@RequiredArgsConstructor
public class FormulairePreferencesController {

    private final FormulairePreferencesService formulaireService;

    /**
     * Récupérer le formulaire par token (accès public depuis le lien email)
     */
    @GetMapping("/token/{token}")
    public ResponseEntity<FormulairePreferencesPaiementDTO> getByToken(@PathVariable String token) {
        return ResponseEntity.ok(formulaireService.getByToken(token));
    }

    /**
     * Soumettre les préférences (accès public via token)
     */
    @PostMapping("/token/{token}/submit")
    public ResponseEntity<Void> submitPreferences(
            @PathVariable String token,
            @RequestBody PreferencesRequest request
    ) {
        formulaireService.submitPreferences(token, request);
        return ResponseEntity.ok().build();
    }

    /**
     * Générer un token pour un enrollment (appelé par le workflow)
     */
    @PostMapping("/generate/{enrollmentId}")
    public ResponseEntity<String> generateToken(@PathVariable Long enrollmentId) {
        return ResponseEntity.ok(formulaireService.genererToken(enrollmentId));
    }
}