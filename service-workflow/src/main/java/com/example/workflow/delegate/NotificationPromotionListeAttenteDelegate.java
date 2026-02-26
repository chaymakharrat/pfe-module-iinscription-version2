package com.example.workflow.delegate.department;

import com.example.workflow.client.EnrollmentServiceClient;
import com.example.workflow.client.NotificationServiceClient;
import com.example.workflow.dto.notification.NotificationRequest;
import com.example.workflow.dto.updateStatusDemande.StatusUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component("notificationPromotionListeAttenteDelegate")
@RequiredArgsConstructor
@Slf4j
public class NotificationPromotionListeAttenteDelegate implements JavaDelegate {

    private final NotificationServiceClient notificationClient;
    private final EnrollmentServiceClient enrollmentClient;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long studentId = (Long) execution.getVariable("studentId");
        Long demandeId = (Long) execution.getVariable("enrollmentId");
        String nomDiplome = (String) execution.getVariable("nomDiplome");

        log.info("🎉 Promotion liste d'attente pour studentId={}", studentId);

        // 1. Mettre à jour statut → DEPARTEMENT_VALIDE
        enrollmentClient.updateStatus(demandeId, StatusUpdateRequest.builder()
                .status("DEPARTEMENT_VALIDE")
                .commentaire("Place libérée - Promu automatiquement depuis la liste d'attente")
                .loginUtilisateur("SYSTEM")
                .date(LocalDateTime.now())
                .build());

        // 2. Mettre à jour statut → EN_ATTENTE_PAIEMENT
        enrollmentClient.updateStatus(demandeId, StatusUpdateRequest.builder()
                .status("EN_ATTENTE_PAIEMENT")
                .commentaire("Génération automatique de la facture")
                .loginUtilisateur("SYSTEM")
                .date(LocalDateTime.now().plusSeconds(30))
                .build());

        // 3. Envoyer notification
        notificationClient.sendNotification(
                NotificationRequest.builder()
                        .studentId(studentId)
                        .demandeId(demandeId)
                        .type("EMAIL")
                        .subject("🎉 Une place est disponible - Action requise !")
                        .message("""
                    Bonjour,

                    Bonne nouvelle ! Une place s'est libérée pour 
                    le diplôme "%s".

                    Votre candidature a été automatiquement promue 
                    depuis la liste d'attente.

                    Vous devez maintenant procéder au paiement de 
                    vos frais d'inscription pour finaliser votre dossier.

                    ⚠️ Merci d'agir rapidement pour confirmer 
                    votre place.

                    Cordialement,
                    Le Département — ITECH University
                    """.formatted(nomDiplome))
                        .build()
        );

        log.info("✅ Promotion et notification envoyées pour studentId={}", studentId);
    }
}
