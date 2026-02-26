package pfe.example.enrollement_module.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pfe.example.enrollement_module.client.CamundaWorkflowClient;
import pfe.example.enrollement_module.client.EtudiantServiceClient; // ⚠️ À CRÉER
import pfe.example.enrollement_module.dto.EtudiantInfoDTO;
import pfe.example.enrollement_module.dto.HistoriqueStatut.HistoriqueRequest;
import pfe.example.enrollement_module.dto.HistoriqueStatut.StatusUpdateRequest;
import pfe.example.enrollement_module.dto.DemandeInscriptionDTO;
import pfe.example.enrollement_module.dto.dashboard.DashboardStatsDTO;
import pfe.example.enrollement_module.dto.dashboard.WorkflowDistributionDTO;
import pfe.example.enrollement_module.dto.workflow.ProcessInstanceResponse;
import pfe.example.enrollement_module.dto.workflow.StartProcessRequest;
import pfe.example.enrollement_module.entities.DemandeInscription;
import pfe.example.enrollement_module.entities.HistoriqueStatus;
import pfe.example.enrollement_module.enumerateur.StatutDemandeInscription;
import pfe.example.enrollement_module.repository.DemandeInscriptionRepository;
import pfe.example.enrollement_module.repository.HistoriqueStatusRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DemandeInscriptionService {

    private final DemandeInscriptionRepository demandeInscriptionRepository;
    private final HistoriqueStatusRepository historiqueRepository;
    private final CamundaWorkflowClient camundaClient;

    public DemandeInscription submitCandidature(DemandeInscription request) {
        DemandeInscription demande = new DemandeInscription();
        demande.setNomDiplome(request.getNomDiplome());
        demande.setLangueDiplome(request.getLangueDiplome());
        demande.setNiveauChoisi(request.getNiveauChoisi());
        demande.setEtudiantId(request.getEtudiantId());
        demande.setStatutActuel(StatutDemandeInscription.SOUMIS);

        DemandeInscription savedDemande = demandeInscriptionRepository.save(demande);

        HistoriqueStatus historique = new HistoriqueStatus();
        historique.setDemandeInscription(savedDemande);
        historique.setStatut(StatutDemandeInscription.SOUMIS);
        historique.setCommentaire("Demande bien soumise");
        historique.setLoginUtilisateur("SYSTEM");
        historique.setDateStatus(LocalDateTime.now());
        historiqueRepository.save(historique);

        try {
            StartProcessRequest processRequest = new StartProcessRequest(
                    savedDemande.getId(),
                    savedDemande.getEtudiantId(),
                    savedDemande.getNomDiplome(),
                    savedDemande.getLangueDiplome()
            );
            ProcessInstanceResponse processInstance =
                    camundaClient.startEnrollmentProcess(processRequest);

            System.out.println("✅ Processus Camunda démarré : "
                    + processInstance.getProcessInstanceId());

        } catch (Exception e) {
            System.err.println("❌ Erreur Camunda : " + e.getMessage());
        }

        return demandeInscriptionRepository.findById(savedDemande.getId())
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));
    }

    // ✅ MÉTHODE COMPLÈTE — était coupée dans ton code
    public void updateStatus(Long id, StatusUpdateRequest request) {
        DemandeInscription demande = demandeInscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée: " + id));

        demande.setStatutActuel(StatutDemandeInscription.valueOf(request.getStatus()));

        // ✅ Générer token si passage en EN_ATTENTE_DOCUMENT
        // ✅ Dans DemandeInscriptionService.java — updateStatus corrigé
        if ("EN_ATTENTE_DOCUMENT".equals(request.getStatus())) {
            boolean tokenInvalide = demande.getTokenAcces() == null
                    || demande.getTokenExpiration() == null
                    || demande.getTokenExpiration().isBefore(LocalDateTime.now());

            if (tokenInvalide) {
                String token = UUID.randomUUID().toString();
                demande.setTokenAcces(token);
                demande.setTokenExpiration(LocalDateTime.now().plusDays(3));
                System.out.println("🔑 Token généré pour dossier " + id + " : " + token);
            }
        }

         //✅ Ajouter à l'historique
        HistoriqueStatus historique = HistoriqueStatus.builder()
                .demandeInscription(demande)
                .statut(StatutDemandeInscription.valueOf(request.getStatus()))
                .commentaire(request.getCommentaire())
                .loginUtilisateur(request.getLoginUtilisateur())
                .dateStatus(LocalDateTime.now())
                .build();

        historiqueRepository.save(historique);
        demandeInscriptionRepository.save(demande);
    }
    public void addHistoriqueFromWorkflow(Long id, HistoriqueRequest request) {
        DemandeInscription demande = demandeInscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        StatutDemandeInscription nouveauStatut =
                StatutDemandeInscription.valueOf(request.getNouveauStatus());

        HistoriqueStatus historique = new HistoriqueStatus();
        historique.setDemandeInscription(demande);
        historique.setStatut(nouveauStatut);
        historique.setDateStatus(LocalDateTime.now());
        historiqueRepository.save(historique);

        demande.setStatutActuel(nouveauStatut);
        demandeInscriptionRepository.save(demande);
    }

    public DemandeInscription getEnrollment(Long id) {
        return demandeInscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));
    }

    public String generateToken(Long id) {
        DemandeInscription demande = demandeInscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        if (demande.getTokenAcces() == null ||
                demande.getTokenExpiration().isBefore(LocalDateTime.now())) {
            demande.setTokenAcces(UUID.randomUUID().toString());
            demande.setTokenExpiration(LocalDateTime.now().plusDays(3));
            demandeInscriptionRepository.save(demande);
        }
        return demande.getTokenAcces();
    }

    public DemandeInscription getDemandeByToken(String token) {
        DemandeInscription demande = demandeInscriptionRepository.findByTokenAcces(token)
                .orElseThrow(() -> new RuntimeException("Lien invalide ou expiré"));

        if (demande.getTokenExpiration() != null &&
                demande.getTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Ce lien a expiré. Veuillez contacter la scolarité.");
        }

        return demande;
    }

    public void resubmitByToken(String token) {
        DemandeInscription demande = getDemandeByToken(token);

        resubmitDocuments(demande.getId());

        // Invalider le token après usage
        demande.setTokenAcces(null);
        demande.setTokenExpiration(null);
        demandeInscriptionRepository.save(demande);
    }

    public void resubmitDocuments(Long id) {
        DemandeInscription demande = demandeInscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        HistoriqueStatus historique = HistoriqueStatus.builder()
                .demandeInscription(demande)
                .statut(StatutDemandeInscription.RELANCE)  // ← RELANCE au lieu de EN_COURS_SCOLARITE
                .commentaire("Documents resoumis par le candidat — en attente de re-vérification.")
                .loginUtilisateur("SYSTEM")
                .dateStatus(LocalDateTime.now())
                .build();

        historiqueRepository.save(historique);
        demande.setStatutActuel(StatutDemandeInscription.RELANCE);
        demandeInscriptionRepository.save(demande);
    }

    public DemandeInscription getDemandeByEtudiantId(Long etudiantId) {
        return demandeInscriptionRepository.findByEtudiantId(etudiantId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée pour cet étudiant"));
    }
    public List<DemandeInscription> getDemandesByDiplome(String nomDiplome, String langue) {
        return demandeInscriptionRepository
                .findByNomDiplomeAndLangueDiplome(nomDiplome, langue);
    }
    public List<DemandeInscription> getDemandesByDiplomeAllLangues(String nomDiplome) {
        return demandeInscriptionRepository.findByNomDiplome(nomDiplome);
    }
}


