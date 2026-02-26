package com.example.workflow.delegate.scolarité;

import com.example.workflow.client.NotificationServiceClient;
import com.example.workflow.dto.notification.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

// delegate/NotificationRejetDelegate.java — VERSION AMÉLIORÉE
@Component("notificationRejetScolariteDelegate")
@RequiredArgsConstructor
@Slf4j
public class NotificationRejetScolariteDelegate implements JavaDelegate {

    private final NotificationServiceClient notificationClient;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long studentId = (Long) execution.getVariable("studentId");
        Long demandeId = (Long) execution.getVariable("enrollmentId");
        String commentaire = (String) execution.getVariable("commentaire");

        NotificationRequest request = NotificationRequest.builder()
                .studentId(studentId)
                .demandeId(demandeId)
                .type("EMAIL")
                .subject("Décision concernant votre dossier d'inscription")
                .message(buildRejetMessage(commentaire))
                .build();

        notificationClient.sendNotification(request);
    }

    private String buildRejetMessage(String commentaire) {
        if (commentaire != null && commentaire.contains("Aperçu de l'email")) {
            return commentaire;
        }
        return """
            Bonjour,

            Après étude de votre dossier d'inscription, nous avons le regret de vous
            informer que votre candidature n'a pas été retenue.

            Motif : %s

            Si vous souhaitez contester cette décision ou obtenir plus d'informations,
            veuillez contacter notre service de scolarité à :
            scolarite@itech-university.tn

            Cordialement,
            Le Service de Scolarité — ITECH University
            """.formatted(commentaire != null ? commentaire : "Non spécifié");
    }
}