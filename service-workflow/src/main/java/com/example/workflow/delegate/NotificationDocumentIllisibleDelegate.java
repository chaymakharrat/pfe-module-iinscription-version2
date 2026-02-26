package com.example.workflow.delegate.scolarité;

import com.example.workflow.client.NotificationServiceClient;
import com.example.workflow.dto.notification.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

// delegate/NotificationDocumentIllisibleDelegate.java
@Component("notificationDocumentIllisibleDelegate")
@RequiredArgsConstructor
@Slf4j
public class NotificationDocumentIllisibleDelegate implements JavaDelegate {

    private final NotificationServiceClient notificationClient;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long studentId = (Long) execution.getVariable("studentId");
        Long demandeId = (Long) execution.getVariable("enrollmentId");
        String commentaire = (String) execution.getVariable("commentaire");
        String documentsManquants = (String) execution.getVariable("documentsManquants");

        String message = buildDocumentIllisibleMessage(commentaire, documentsManquants);

        NotificationRequest request = NotificationRequest.builder()
                .studentId(studentId)
                .demandeId(demandeId)
                .type("EMAIL")
                .subject("Action requise — Document(s) à resoumettre")
                .message(message)
                .build();

        notificationClient.sendNotification(request);
        log.info("📧 Notification document illisible envoyée - Demande {}", demandeId);
    }

    private String buildDocumentIllisibleMessage(String commentaire, String docs) {
        if (commentaire != null && commentaire.contains("Aperçu de l'email")) {
            return commentaire;
        }
        return """
            Bonjour,

            Suite à l'examen de votre dossier d'inscription, nous avons constaté que
            certains documents soumis sont illisibles ou non conformes.

            Motif : %s
            Document(s) concerné(s) : %s

            Nous vous invitons à vous connecter à votre espace candidat et à 
            resoumettre les pièces demandées dans un délai de 5 jours ouvrés.

            Lien : https://itech-university.tn/mon-dossier

            Cordialement,
            Le Service de Scolarité — ITECH University
            """.formatted(
                commentaire != null ? commentaire : "Document illisible",
                docs != null ? docs : "Voir votre espace candidat"
        );
    }
}
