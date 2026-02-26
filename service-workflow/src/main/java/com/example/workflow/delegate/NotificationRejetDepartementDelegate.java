package com.example.workflow.delegate.department;

import com.example.workflow.client.NotificationServiceClient;
import com.example.workflow.dto.notification.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

// delegate/NotificationRejetDepartementDelegate.java — VERSION AMÉLIORÉE
@Component("notificationRejetDepartementDelegate")
@RequiredArgsConstructor
@Slf4j
public class NotificationRejetDepartementDelegate implements JavaDelegate {

    private final NotificationServiceClient notificationClient;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long studentId = (Long) execution.getVariable("studentId");
        Long demandeId = (Long) execution.getVariable("enrollmentId");
        String commentaire = (String) execution.getVariable("commentaire");

        log.info("📧 Envoi notification de rejet département pour studentId={}", studentId);

        NotificationRequest request = NotificationRequest.builder()
                .studentId(studentId)
                .demandeId(demandeId)
                .type("EMAIL")
                .subject("Décision du Département concernant votre candidature")
                .message(buildRejetDepartementMessage(commentaire))
                .build();

        notificationClient.sendNotification(request);

        log.info("✅ Notification de rejet département envoyée");
    }

    private String buildRejetDepartementMessage(String commentaire) {
        if (commentaire != null && commentaire.contains("Aperçu de l'email")) {
            return commentaire;
        }

        return """
            Bonjour,

            Après évaluation pédagogique de votre dossier par le Département,
            nous regrettons de vous informer que votre candidature n’a pas été retenue.

            Motif : %s

            Pour toute information complémentaire, vous pouvez contacter le
            secrétariat du département concerné.

            Cordialement,
            Le Département — ITECH University
            """.formatted(commentaire != null ? commentaire : "Non spécifié");
    }
}