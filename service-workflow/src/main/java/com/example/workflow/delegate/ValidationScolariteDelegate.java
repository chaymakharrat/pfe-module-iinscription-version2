package com.example.workflow.delegate.scolarité;

import com.example.workflow.client.EnrollmentServiceClient;
import com.example.workflow.dto.updateStatusDemande.StatusUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component("validationScolariteDelegate")
@RequiredArgsConstructor
@Slf4j
public class ValidationScolariteDelegate implements TaskListener {

    private final EnrollmentServiceClient enrollmentClient;

    @Override
    public void notify(DelegateTask delegateTask) {
        Long enrollmentId = (Long) delegateTask.getVariable("enrollmentId");
        String decision = (String) delegateTask.getVariable("decision");
        String commentaire = (String) delegateTask.getVariable("commentaire");
        String loginUtilisateur = (String) delegateTask.getVariable("loginUtilisateur");

        log.info("📋 Validation scolarité - Demande {}: {}", enrollmentId, decision);

        // ✅ Les 3 cas gérés correctement
        String nouveauStatut = switch (decision) {
            case "ACCEPTE"                    -> "SCOLARITE_VALIDEE";
            case "REJETE"                     -> "REJETE_SCOLARITE";
            case "DEMANDE_PIECES",
                 "DOCUMENT_ILLISIBLE"         -> "EN_ATTENTE_DOCUMENT"; // ← les deux acceptés
            default -> throw new IllegalArgumentException("Décision inconnue: " + decision);
        };

// Et aligner la condition en dessous
        if ("DEMANDE_PIECES".equals(decision) || "DOCUMENT_ILLISIBLE".equals(decision)) {
            delegateTask.setVariable("documentsManquants", commentaire);
        }

        StatusUpdateRequest statusUpdate = StatusUpdateRequest.builder()
                .status(nouveauStatut)
                .commentaire(commentaire)
                .loginUtilisateur(loginUtilisateur)
                .date(LocalDateTime.now())
                .build();

        enrollmentClient.updateStatus(enrollmentId, statusUpdate);

        // Passer au département uniquement si ACCEPTE
        if ("ACCEPTE".equals(decision)) {
            StatusUpdateRequest statusUpdate2 = StatusUpdateRequest.builder()
                    .status("EN_COURS_DEPARTEMENT")
                    .commentaire("Dossier transmis au département")
                    .loginUtilisateur("SYSTEM")
                    .date(LocalDateTime.now().plusMinutes(1))
                    .build();
            enrollmentClient.updateStatus(enrollmentId, statusUpdate2);
        }

        log.info("✅ Demande {} → statut: {}", enrollmentId, nouveauStatut);
    }
}