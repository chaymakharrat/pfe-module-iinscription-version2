package com.example.workflow.delegate;// delegate/VerifyDocumentDelegate.java

import com.example.workflow.client.EnrollmentServiceClient;
import com.example.workflow.dto.StatusUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("verifyDocumentDelegate")
@RequiredArgsConstructor
@Slf4j
public class VerifyDocumentDelegate implements JavaDelegate {

    private final EnrollmentServiceClient enrollmentClient;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long enrollmentId = (Long) execution.getVariable("enrollmentId");

        log.info("🔍 Vérification des documents pour la demande: {}", enrollmentId);

        // Mettre à jour le statut vers EN_COURS_SCOLARITE
        StatusUpdateRequest statusUpdate = new StatusUpdateRequest();
        statusUpdate.setStatus("EN_COURS_SCOLARITE");
        statusUpdate.setCommentaire("Documents vérifiés, en attente de validation scolarité");
        statusUpdate.setModifiePar("SYSTEM");

        enrollmentClient.updateStatus(enrollmentId, statusUpdate);

        log.info("✅ Documents vérifiés, statut mis à jour");
    }
}