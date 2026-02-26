package com.example.workflow.delegate.department;

import com.example.workflow.client.NotificationServiceClient;
import com.example.workflow.dto.notification.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("notificationListeAttenteDelegate")
@RequiredArgsConstructor
@Slf4j
public class NotificationListeAttenteDelegate implements JavaDelegate {

    private final NotificationServiceClient notificationClient;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long studentId = (Long) execution.getVariable("studentId");
        Long demandeId = (Long) execution.getVariable("enrollmentId");
        String nomDiplome = (String) execution.getVariable("nomDiplome");
        Integer positionAttente = (Integer) execution.getVariable("positionAttente");

        log.info("📧 Notification liste d'attente pour studentId={}", studentId);

        NotificationRequest request = NotificationRequest.builder()
                .studentId(studentId)
                .demandeId(demandeId)
                .type("EMAIL")
                .subject("🕐 Votre candidature - Liste d'attente")
                .message("""
                    Bonjour,

                    Votre candidature pour le diplôme "%s" a été évaluée 
                    favorablement par le Département pédagogique.

                    Cependant, la capacité maximale de la formation est 
                    actuellement atteinte.

                    Vous êtes inscrit(e) en liste d'attente 
                    (position : %s).

                    Vous serez contacté(e) dès qu'une place se libère.

                    Cordialement,
                    Le Département — ITECH University
                    """.formatted(
                        nomDiplome,
                        positionAttente != null ? "#" + positionAttente : "en cours"
                ))
                .build();

        notificationClient.sendNotification(request);
        log.info("✅ Notification liste d'attente envoyée pour studentId={}", studentId);
    }
}
