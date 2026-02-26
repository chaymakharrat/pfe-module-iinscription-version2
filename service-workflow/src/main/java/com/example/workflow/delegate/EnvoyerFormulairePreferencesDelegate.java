package com.example.workflow.delegate.department;

import com.example.workflow.client.EnrollmentServiceClient;
import com.example.workflow.client.FinanceServiceClient;
import com.example.workflow.client.NotificationServiceClient;
import com.example.workflow.dto.facture.RemiseDTO;
import com.example.workflow.dto.notification.NotificationRequest;
import com.example.workflow.dto.updateStatusDemande.StatusUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component("envoyerFormulairePreferencesDelegate")
@RequiredArgsConstructor
@Slf4j
public class EnvoyerFormulairePreferencesDelegate implements JavaDelegate {

    private final NotificationServiceClient notificationClient;
    private final FinanceServiceClient financeClient;
    private final EnrollmentServiceClient enrollmentClient;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long studentId = (Long) execution.getVariable("studentId");
        Long enrollmentId = (Long) execution.getVariable("enrollmentId");
        String nomDiplome = (String) execution.getVariable("nomDiplome");

        // Récupérer les remises disponibles
        List<RemiseDTO> remisesDisponibles = financeClient.getRemisesDisponibles();

        // Générer token pour le formulaire
        String token = financeClient.genererTokenFormulaire(enrollmentId);
        execution.setVariable("tokenFormulaire", token);

        // Construire le lien du formulaire
        String lienFormulaire = "https://itech.tn/paiement/formulaire?token=" + token;

        // Construire la liste des remises
        String remisesHtml = remisesDisponibles.stream()
                .map(r -> "- " + r.getMotif() + " : " + r.getPourcentage() + "%")
                .collect(Collectors.joining("\n"));

        // Envoyer email au candidat
        notificationClient.sendNotification(
                NotificationRequest.builder()
                        .studentId(studentId)
                        .demandeId(enrollmentId)
                        .type("EMAIL")
                        .subject("📋 Formulaire de préférences de paiement - " + nomDiplome)
                        .message("""
                    Bonjour,

                    Votre candidature pour le diplôme "%s" a été validée.

                    Merci de remplir le formulaire de préférences
                    de paiement via le lien ci-dessous :

                    %s

                    📌 Remises disponibles (cochez si applicable) :
                    %s

                    💳 Modes de paiement : En ligne / En présentiel
                    💰 Type : Total ou Partiel (mensualités au choix)

                    ⚠️ Vous avez 3 jours pour répondre.
                    Sans réponse, votre dossier sera automatiquement rejeté.

                    Cordialement,
                    Le Service Financier — ITECH University
                    """.formatted(nomDiplome, lienFormulaire, remisesHtml))
                        .build()
        );

        // Mettre à jour statut
        enrollmentClient.updateStatus(enrollmentId, StatusUpdateRequest.builder()
                .status("EN_ATTENTE_PAIEMENT")
                .commentaire("Formulaire de préférences envoyé au candidat")
                .loginUtilisateur("SYSTEM")
                .date(LocalDateTime.now())
                .build());

        log.info("✅ Formulaire préférences envoyé pour enrollment={}", enrollmentId);
    }
}
