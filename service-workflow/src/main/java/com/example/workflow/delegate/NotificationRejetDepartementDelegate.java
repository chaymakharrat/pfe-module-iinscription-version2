package com.example.workflow.delegate;// delegate/NotificationRejetDelegate.java

import com.example.workflow.client.NotificationServiceClient;
import com.example.workflow.dto.notification.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("notificationRejetDelegate")
@RequiredArgsConstructor
@Slf4j
public class NotificationRejetDelegate implements JavaDelegate {

    private final NotificationServiceClient notificationClient;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long studentId = (Long) execution.getVariable("studentId");
        String commentaire = (String) execution.getVariable("commentaire");

        log.info("📧 Envoi notification de rejet à l'étudiant: {}", studentId);

        NotificationRequest notification = NotificationRequest.builder()
                .studentId(studentId)
                .type("EMAIL")
                .subject("Demande d'inscription - Décision")
                .message("Votre demande d'inscription n'a pas été acceptée. Raison: " +
                        (commentaire != null ? commentaire : "Non spécifiée"))
                .build();

        notificationClient.sendNotification(notification);

        log.info("✅ Notification de rejet envoyée");
    }
}