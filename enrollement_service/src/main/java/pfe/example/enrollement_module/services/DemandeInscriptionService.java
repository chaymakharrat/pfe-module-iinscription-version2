package pfe.example.enrollement_module.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pfe.example.enrollement_module.client.CamundaWorkflowClient;
import pfe.example.enrollement_module.dto.*;
import pfe.example.enrollement_module.entities.DemandeInscription;
import pfe.example.enrollement_module.entities.HistoriqueStatus;
import pfe.example.enrollement_module.enumerateur.StatutDemandeInscription;
import pfe.example.enrollement_module.repository.DemandeInscriptionRepository;
import pfe.example.enrollement_module.repository.HistoriqueStatusRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DemandeInscriptionService {

    private final DemandeInscriptionRepository demandeInscriptionRepository;
    private final HistoriqueStatusRepository historiqueRepository;
    private final CamundaWorkflowClient camundaClient;

    public DemandeInscription submitCandidature(DemandeInscription request) {
        DemandeInscription demande = new DemandeInscription();

        // Obligatoire
        demande.setNomDiplome(request.getNomDiplome());
        demande.setEtudiantId(request.getEtudiantId());

        // Statut initial
        //demande.setStatut(StatutDemandeInscription.SOUMIS);

        // dateCreation déjà gérée par défaut dans l'entité
        System.out.println("NomDiplome = " + request.getNomDiplome());
        System.out.println("EtudiantId = " + request.getEtudiantId());


        // Sauvegarder
        DemandeInscription savedDemande = demandeInscriptionRepository.save(demande);

        // Historique
        HistoriqueStatus historique = new HistoriqueStatus();
        historique.setDemandeInscription(savedDemande);
        historique.setStatut(StatutDemandeInscription.SOUMIS);
        historique.setDateStatus(LocalDateTime.now());
        historiqueRepository.save(historique);
        // 4️⃣ 🆕 Démarrer le processus Camunda
        try {
            StartProcessRequest processRequest = new StartProcessRequest(
                    savedDemande.getId(),
                    savedDemande.getEtudiantId(),
                    savedDemande.getNomDiplome()
            );

            ProcessInstanceResponse processInstance =
                    camundaClient.startEnrollmentProcess(processRequest);

            // 5️⃣ Sauvegarder l'ID du processus dans la demande
            savedDemande.setProcessInstanceId(processInstance.getProcessInstanceId());
            demandeInscriptionRepository.save(savedDemande);

            System.out.println("✅ Processus Camunda démarré : " + processInstance.getProcessInstanceId());

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du démarrage du processus Camunda : " + e.getMessage());
            // Vous pouvez gérer l'exception comme vous voulez
            // Pour l'instant, on continue même si Camunda n'est pas disponible
        }

        return savedDemande;
    }
    // 🆕 Appelé par Workflow
    public void addHistoriqueFromWorkflow(Long id, HistoriqueRequest request) {
        DemandeInscription demande = demandeInscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        HistoriqueStatus historique = new HistoriqueStatus();
        historique.setDemandeInscription(demande);
        historique.setStatut(StatutDemandeInscription.valueOf(request.getNouveauStatus()));
        historique.setDateStatus(LocalDateTime.now());
        historiqueRepository.save(historique);
    }
    // 🆕 Récupérer une demande (appelé par Workflow)
    public DemandeInscription getEnrollment(Long id) {
        return demandeInscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));
    }

    // 🆕 Appelé par Workflow
    public void updateStatus(Long id, StatusUpdateRequest request) {
        DemandeInscription demande = demandeInscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        HistoriqueStatus historique = new HistoriqueStatus();
        historique.setDemandeInscription(demande);
        historique.setStatut(StatutDemandeInscription.valueOf(request.getStatus()));
        historique.setDateStatus(LocalDateTime.now());
        historiqueRepository.save(historique);
    }

}
