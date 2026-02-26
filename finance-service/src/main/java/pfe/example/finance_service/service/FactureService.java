package pfe.example.finance_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pfe.example.finance_service.DTO.DemandeInfoDTO;
import pfe.example.finance_service.DTO.FormulairePreferencesPaiementDTO;
import pfe.example.finance_service.client.DepartementServiceClient;
import pfe.example.finance_service.client.EnrollmentServiceClient;
import pfe.example.finance_service.entities.*;
import pfe.example.finance_service.enumerateur.ModePaiement;
import pfe.example.finance_service.enumerateur.StatusEcheance;
import pfe.example.finance_service.enumerateur.StatusPaiement;
import pfe.example.finance_service.enumerateur.TypePaiement;
import pfe.example.finance_service.exception.ResourceNotFoundException;
import pfe.example.finance_service.repositories.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FactureService {

    private final FactureRepository factureRepository;
    private final EcheanceRepository echeanceRepository;
    private final RemiseRepository remiseRepository;
    private final FormulairePreferencesRepository formulaireRepository;
    private final DepartementServiceClient departementClient;
    private final PaiementRepository paiementRepository;
    private final EnrollmentServiceClient enrollmentClient;

    // Appelé par l'agent finance depuis le dashboard
    public Facture genererFacture(Long enrollmentId, String loginAgent) {

        // 1. Récupérer les préférences du candidat
        FormulairePreferences prefs = formulaireRepository
                .findByEnrollmentId(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Formulaire non trouvé pour enrollment: " + enrollmentId));

        // 2. Récupérer frais depuis department-service
        // ✅ Récupérer nomDiplome + langueDiplome depuis enrollment-service
        DemandeInfoDTO demande = enrollmentClient.getDemandeInfo(enrollmentId);

        // Récupérer frais avec nomDiplome ET langue
        double fraisBase = departementClient.getFraisInscription(
                demande.getNomDiplome(),
                demande.getLangueDiplome()
        );

        // 3. Calculer remises
        double totalRemise = 0;
        List<Remise> remisesAppliquees = new ArrayList<>();

        for (Long remiseId : prefs.getRemisesSelectionnees()) {
            Remise remise = remiseRepository.findById(remiseId)
                    .orElse(null);
            if (remise != null && remise.isActif()) {
                totalRemise += remise.getPourcentage();
                remisesAppliquees.add(remise);
            }
        }

        double montantFinal = fraisBase * (1 - totalRemise / 100);
        LocalDate dateLimite = prefs.getTypePaiement() == TypePaiement.TOTAL
                ? LocalDate.now().plusDays(7)
                : LocalDate.now().plusMonths(prefs.getFrequenceMois());

        // 4. Créer la facture
        Facture facture = Facture.builder()
                .dateLimitePaiement(dateLimite)
                .dateGeneration(LocalDate.now())
                .montantTotal(montantFinal)
                .montantPaye(0)
                .montantRestant(montantFinal)
                .numeroFacture(genererNumeroFacture())
                .statusPaiement(StatusPaiement.EN_ATTENTE)
                .typePaiement(prefs.getTypePaiement())
                .modePaiement(prefs.getModePaiement())
                .frequenceMois(prefs.getFrequenceMois())
                .enrollmentId(enrollmentId)
                .build();

        facture = factureRepository.save(facture);

        // Lier remises à la facture
        for (Remise r : remisesAppliquees) {
            r.setFacture(facture);
            remiseRepository.save(r);
        }

        // 5. Générer échéances si PARTIEL
        if (prefs.getTypePaiement() == TypePaiement.PARTIEL
                && prefs.getFrequenceMois() != null) {
            genererEcheances(facture, prefs.getFrequenceMois());
        }
        // Si TOTAL → pas d'échéances

        log.info("✅ Facture {} générée - Montant: {}", facture.getNumeroFacture(), montantFinal);
        return facture;
    }

    private void genererEcheances(Facture facture, int frequenceMois) {
        // Nombre d'échéances = durée totale / fréquence
        // Exemple : 3 mois avec fréquence 1 = 3 échéances
        int nombreEcheances = 3; // configurable
        double montantParEcheance = facture.getMontantTotal() / nombreEcheances;

        for (int i = 1; i <= nombreEcheances; i++) {
            Echeance echeance = Echeance.builder()
                    .dateEcheance(LocalDate.now().plusMonths((long) i * frequenceMois))
                    .montantAPayer(montantParEcheance)
                    .numeroEcheance("ECH-" + facture.getId() + "-00" + i)
                    .numeroOrdre(i)
                    .statut(StatusEcheance.EN_ATTENTE)
                    .facture(facture)
                    .build();
            echeanceRepository.save(echeance);
        }
    }

    // Token pour sécuriser le formulaire
    public String genererTokenFormulaire(Long enrollmentId) {
        String token = UUID.randomUUID().toString();
        FormulairePreferences formPrefs = FormulairePreferences.builder()
                .enrollmentId(enrollmentId)
                .token(token)
                .reponseSoumise(false)
                .build();
        formulaireRepository.save(formPrefs);
        return token;
    }

    // Soumettre les préférences (appelé depuis le formulaire Angular)
    public void soumettrePreferences(String token,
                                     FormulairePreferencesPaiementDTO dto) {
        FormulairePreferences prefs = formulaireRepository
                .findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token invalide"));

        prefs.setModePaiement(dto.getModePaiement());
        prefs.setTypePaiement(dto.getTypePaiement());
        prefs.setFrequenceMois(dto.getFrequenceMois());
        prefs.setRemisesSelectionnees(dto.getRemisesSelectionnees());
        prefs.setReponseSoumise(true);
        prefs.setDateReponse(LocalDateTime.now());

        formulaireRepository.save(prefs);
        log.info("✅ Préférences soumises pour enrollment={}", prefs.getEnrollmentId());
    }

    // Enregistrer un paiement
    public Paiement enregistrerPaiement(Long echeanceId,
                                        double montant,
                                        ModePaiement mode) {
        Echeance echeance = echeanceRepository.findById(echeanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Échéance non trouvée"));

        Paiement paiement = Paiement.builder()
                .datePaiement(LocalDate.now())
                .montantAPayer(montant)
                .numeroPaiement("PAY-" + UUID.randomUUID()
                        .toString().substring(0, 8).toUpperCase())
                .modePaiement(mode)
                .echeance(echeance)
                .build();

        echeance.setStatut(StatusEcheance.PAYE);
        echeanceRepository.save(echeance);

        // Mettre à jour la facture
        Facture facture = echeance.getFacture();
        facture.setMontantPaye(facture.getMontantPaye() + montant);
        facture.setMontantRestant(facture.getMontantTotal() - facture.getMontantPaye());

        if (facture.getMontantRestant() <= 0) {
            facture.setStatusPaiement(StatusPaiement.PAYE);
        } else {
            facture.setStatusPaiement(StatusPaiement.PARTIEL);
        }
        factureRepository.save(facture);

        return paiementRepository.save(paiement);
    }

    private String genererNumeroFacture() {
        return "FACT-" + LocalDate.now().getYear() + "-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
