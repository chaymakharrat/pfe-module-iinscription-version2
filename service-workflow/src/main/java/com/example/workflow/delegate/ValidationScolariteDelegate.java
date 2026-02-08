package com.example.workflow.delegate;// delegate/ValidationScolariteDelegate.java

import com.example.workflow.client.EnrollmentServiceClient;
import com.example.workflow.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("validationScolariteDelegate")
@RequiredArgsConstructor
@Slf4j
public class ValidationScolariteDelegate implements JavaDelegate {

    private final EnrollmentServiceClient enrollmentClient;
    //private final NotificationServiceClient notificationClient;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long enrollmentId = (Long) execution.getVariable("enrollmentId");
        Long studentId = (Long) execution.getVariable("studentId");
        String decision = (String) execution.getVariable("decision");
        String commentaire = (String) execution.getVariable("commentaire");

        log.info("📋 Traitement validation scolarité pour demande {}: {}", enrollmentId, decision);

        if ("ACCEPTE".equals(decision)) {
            // Mise à jour statut -> SCOLARITE_VALIDEE
            StatusUpdateRequest statusUpdate = StatusUpdateRequest.builder()
                    .status("SCOLARITE_VALIDEE")
                    .commentaire(commentaire != null ? commentaire : "Validé par la scolarité")
                    .modifiePar("SCOLARITE")
                    .build();

            enrollmentClient.updateStatus(enrollmentId, statusUpdate);

            // Notification de succès
            NotificationRequest notification = NotificationRequest.builder()
                    .studentId(studentId)
                    .type("EMAIL")
                    .subject("Validation scolarité - Étape franchie")
                    .message("Votre dossier a été validé par le service scolarité.")
                    .build();

            //notificationClient.sendNotification(notification);

            log.info("✅ Scolarité validée pour demande: {}", enrollmentId);

        } else {
            // Rejet
            StatusUpdateRequest statusUpdate = StatusUpdateRequest.builder()
                    .status("REJETE_SCOLARITE")
                    .commentaire(commentaire != null ? commentaire : "Rejeté par la scolarité")
                    .modifiePar("SCOLARITE")
                    .build();

            enrollmentClient.updateStatus(enrollmentId, statusUpdate);

            log.warn("❌ Demande {} rejetée par la scolarité", enrollmentId);
        }
    }
}