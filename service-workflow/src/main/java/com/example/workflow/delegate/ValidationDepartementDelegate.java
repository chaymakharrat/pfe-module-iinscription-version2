package com.example.workflow.delegate;// delegate/ValidationDepartementDelegate.java

import com.example.workflow.client.EnrollmentServiceClient;
import com.example.workflow.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("validationDepartementDelegate")
@RequiredArgsConstructor
@Slf4j
public class ValidationDepartementDelegate implements JavaDelegate {

    private final EnrollmentServiceClient enrollmentClient;
    //private final NotificationServiceClient notificationClient;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long enrollmentId = (Long) execution.getVariable("enrollmentId");
        Long studentId = (Long) execution.getVariable("studentId");
        String decision = (String) execution.getVariable("decision");
        String commentaire = (String) execution.getVariable("commentaire");

        log.info("🎓 Traitement validation département pour demande {}: {}", enrollmentId, decision);

        if ("ACCEPTE".equals(decision)) {
            StatusUpdateRequest statusUpdate = StatusUpdateRequest.builder()
                    .status("DEPARTEMENT_VALIDE")
                    .commentaire(commentaire != null ? commentaire : "Validé par le département")
                    .modifiePar("DEPARTEMENT")
                    .build();

            enrollmentClient.updateStatus(enrollmentId, statusUpdate);

            NotificationRequest notification = NotificationRequest.builder()
                    .studentId(studentId)
                    .type("EMAIL")
                    .subject("Validation département - Étape franchie")
                    .message("Votre candidature a été acceptée par le département pédagogique.")
                    .build();

            //notificationClient.sendNotification(notification);

            log.info("✅ Département validé pour demande: {}", enrollmentId);

        } else {
            StatusUpdateRequest statusUpdate = StatusUpdateRequest.builder()
                    .status("REJETE_DEPARTEMENT")
                    .commentaire(commentaire != null ? commentaire : "Rejeté par le département")
                    .modifiePar("DEPARTEMENT")
                    .build();

            enrollmentClient.updateStatus(enrollmentId, statusUpdate);

            log.warn("❌ Demande {} rejetée par le département", enrollmentId);
        }
    }
}