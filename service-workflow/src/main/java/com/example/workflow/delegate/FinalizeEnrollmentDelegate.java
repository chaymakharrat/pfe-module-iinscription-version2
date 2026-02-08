package com.example.workflow.delegate;// delegate/FinalizeEnrollmentDelegate.java

import com.example.workflow.client.EnrollmentServiceClient;
import com.example.workflow.client.NotificationServiceClient;
import com.example.workflow.client.StudentServiceClient;
import com.example.workflow.dto.MatriculeResponse;
import com.example.workflow.dto.NotificationRequest;
import com.example.workflow.dto.StatusUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("finalizeEnrollmentDelegate")
@RequiredArgsConstructor
@Slf4j
public class FinalizeEnrollmentDelegate implements JavaDelegate {

    private final StudentServiceClient studentClient;
    private final EnrollmentServiceClient enrollmentClient;
    private final NotificationServiceClient notificationClient;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long enrollmentId = (Long) execution.getVariable("enrollmentId");
        Long studentId = (Long) execution.getVariable("studentId");

        log.info("🎉 Finalisation de l'inscription pour la demande: {}", enrollmentId);

        // 1. Générer le matricule
        MatriculeResponse matricule = studentClient.updateEtudiant(studentId);

        // 2. Activer le compte étudiant
        //studentClient.activateStudent(studentId);

        // 3. Mettre à jour le statut final
        StatusUpdateRequest statusUpdate = StatusUpdateRequest.builder()
                .status("INSCRIT")
                .commentaire("Inscription finalisée - Matricule: " + matricule.getMatricule())
                .modifiePar("SYSTEM")
                .build();

        enrollmentClient.updateStatus(enrollmentId, statusUpdate);

        // 4. Envoyer notification de succès
        NotificationRequest notification = NotificationRequest.builder()
                .studentId(studentId)
                .type("EMAIL")
                .subject("Félicitations - Inscription finalisée!")
                .message("Votre inscription est finalisée. Votre matricule est: " + matricule.getMatricule())
                .build();

        notificationClient.sendNotification(notification);

        log.info("✅ Inscription finalisée avec succès - Matricule: {}", matricule.getMatricule());
    }
}