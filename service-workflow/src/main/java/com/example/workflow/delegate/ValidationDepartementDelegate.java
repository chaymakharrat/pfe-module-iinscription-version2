package com.example.workflow.delegate.department;

import com.example.workflow.client.EnrollmentServiceClient;
import com.example.workflow.dto.updateStatusDemande.StatusUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * TaskListener pour la validation du département ou traitement d’une étape manuelle.
 * Remplace la logique de ValidationPaiementDelegate par un TaskListener.
 */
@Component("validationDepartementDelegate") // même nom que dans le BPMN
@RequiredArgsConstructor
@Slf4j
public class ValidationDepartementDelegate implements TaskListener {

    private final EnrollmentServiceClient enrollmentClient;

    @Override
    public void notify(DelegateTask delegateTask) {
        Long enrollmentId = (Long) delegateTask.getVariable("enrollmentId");
        Boolean isPaid = (Boolean) delegateTask.getVariable("isPaid"); // si on veut réutiliser
        String decision = (String) delegateTask.getVariable("decision"); // "ACCEPTE" ou autre
        String commentaire = (String) delegateTask.getVariable("commentaire");
        String loginUtilisateur = (String) delegateTask.getVariable("loginUtilisateur");

        log.info("📋 Traitement département/finance - Demande {}: {}", enrollmentId, decision != null ? decision : isPaid);

        StatusUpdateRequest statusUpdate;

        if (decision != null) {
            // Traitement département
            statusUpdate = StatusUpdateRequest.builder()
                    .status("ACCEPTE".equals(decision) ? "DEPARTEMENT_VALIDE" : "REJETE_DEPARTEMENT")
                    .commentaire(commentaire != null ? commentaire :
                            ("ACCEPTE".equals(decision) ? "Validé par le département" : "Rejeté par le département"))
                    .loginUtilisateur(loginUtilisateur)
                    .date(LocalDateTime.now())
                    .build();

            enrollmentClient.updateStatus(enrollmentId, statusUpdate);

            if ("ACCEPTE".equals(decision)) {
                // passer au statut en attente paiement
                StatusUpdateRequest statusUpdate2 = StatusUpdateRequest.builder()
                        .status("EN_ATTENTE_PAIEMENT")
                        .commentaire(commentaire)
                        .loginUtilisateur(loginUtilisateur)
                        .date(LocalDateTime.now().plusMinutes(1))
                        .build();

                enrollmentClient.updateStatus(enrollmentId, statusUpdate2);
            }
        } else if (isPaid != null) {
            // Traitement paiement (comme dans ValidationPaiementDelegate)
            if (Boolean.TRUE.equals(isPaid)) {
                statusUpdate = StatusUpdateRequest.builder()
                        .status("PAIEMENT_VALIDE")
                        .commentaire("Paiement validé par le service financier")
                        .loginUtilisateur("FINANCE")
                        .date(LocalDateTime.now())
                        .build();

                enrollmentClient.updateStatus(enrollmentId, statusUpdate);

                log.info("✅ Paiement validé pour demande: {}", enrollmentId);
            } else {
                log.warn("⚠️ Paiement non validé pour demande: {}", enrollmentId);
            }
        }

        log.info("✅ Demande {} mise à jour par TaskListener (validation département/finance)", enrollmentId);
    }
}