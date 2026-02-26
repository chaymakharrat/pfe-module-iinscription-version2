package pfe.example.finance_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;
import pfe.example.finance_service.DTO.FormulairePreferencesPaiementDTO;
import pfe.example.finance_service.entities.FormulairePreferences;
import pfe.example.finance_service.enumerateur.ModePaiement;
import pfe.example.finance_service.enumerateur.TypePaiement;
import pfe.example.finance_service.repositories.FormulairePreferencesRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FormulairePreferencesService {

    private final FormulairePreferencesRepository formulaireRepo;
    // Optionnel : Feign vers enrollment-service pour récupérer nomDiplome
    // private final EnrollmentServiceClient enrollmentClient;

    /**
     * Génère (ou retourne l'existant) un token pour un enrollment.
     * Appelé par EnvoyerFormulairePreferencesDelegate dans le workflow.
     */
    @Transactional
    public String genererToken(Long enrollmentId) {
        // Si un formulaire existe déjà pour cet enrollment, retourner son token
        return formulaireRepo.findByEnrollmentId(enrollmentId)
                .map(f -> {
                    // Réinitialiser l'expiration si relancé
                    log.info("Formulaire existant pour enrollment {} — token réutilisé", enrollmentId);
                    return f.getToken();
                })
                .orElseGet(() -> {
                    // Créer un nouveau formulaire vierge
                    String token = UUID.randomUUID().toString();
                    FormulairePreferences formulaire = FormulairePreferences.builder()
                            .enrollmentId(enrollmentId)
                            .token(token)
                            .reponseSoumise(false)
                            .build();
                    formulaireRepo.save(formulaire);
                    log.info("✅ Nouveau formulaire créé pour enrollment {} — token: {}", enrollmentId, token);
                    return token;
                });
    }

    /**
     * Récupère le formulaire par token (vue publique depuis le lien email).
     * Lance une exception si le token est invalide.
     */
    public FormulairePreferencesPaiementDTO getByToken(String token) {
        FormulairePreferences formulaire = formulaireRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalide: " + token));

        // Vérifier expiration (3 jours après la date de création du formulaire)
        // Note: si tu n'as pas de dateCreation sur l'entité, ajoute-la dans FormulairePreferences
        // Pour l'instant on renvoie simplement le formulaire

        return toDTO(formulaire);
    }

    /**
     * Soumettre les préférences du candidat.
     * Déclenche ensuite le workflow (signal Camunda ou via REST).
     */
    @Transactional
    public void submitPreferences(String token, PreferencesRequest request) {
        FormulairePreferences formulaire = formulaireRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalide: " + token));

        if (formulaire.isReponseSoumise()) {
            throw new RuntimeException("Ce formulaire a déjà été soumis.");
        }

        // Mapper les préférences
        formulaire.setModePaiement(ModePaiement.valueOf(request.getModePaiement()));
        formulaire.setTypePaiement(TypePaiement.valueOf(request.getTypePaiement()));
        formulaire.setFrequenceMois(request.getFrequenceMois());
        formulaire.setRemisesSelectionnees(request.getRemisesSelectionnees());
        formulaire.setDateReponse(LocalDateTime.now());
        formulaire.setReponseSoumise(true);

        formulaireRepo.save(formulaire);

        log.info("✅ Préférences soumises pour enrollment {} — mode={}, type={}",
                formulaire.getEnrollmentId(),
                formulaire.getModePaiement(),
                formulaire.getTypePaiement());

        // TODO : notifier le workflow Camunda que le formulaire a été soumis
        // Exemple : signalWorkflow(formulaire.getEnrollmentId());
    }

    // ─── MAPPER ───────────────────────────────────────────────────────────────

    private FormulairePreferencesPaiementDTO toDTO(FormulairePreferences f) {
        return FormulairePreferencesPaiementDTO.builder()
                .id(f.getId())
                .enrollmentId(f.getEnrollmentId())
                .token(f.getToken())
                .reponseSoumise(f.isReponseSoumise())
                .modePaiement(f.getModePaiement())
                .typePaiement(f.getTypePaiement())
                .frequenceMois(f.getFrequenceMois())
                .remisesSelectionnees(f.getRemisesSelectionnees())
                // dateExpiration : à calculer si tu stockes dateCreation
                // .dateExpiration(f.getDateCreation().plusDays(3))
                .build();
    }
}