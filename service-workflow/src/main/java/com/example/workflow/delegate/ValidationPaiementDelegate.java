package com.example.workflow.delegate;

import com.example.workflow.client.EnrollmentServiceClient;
import com.example.workflow.dto.StatusUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("validationPaiementDelegate")
@RequiredArgsConstructor
@Slf4j
public class ValidationPaiementDelegate implements JavaDelegate {

    private final EnrollmentServiceClient enrollmentClient;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long enrollmentId = (Long) execution.getVariable("enrollmentId");
        Boolean isPaid = (Boolean) execution.getVariable("isPaid");

        log.info("💳 Traitement validation paiement pour demande {}: {}", enrollmentId, isPaid);

        if (Boolean.TRUE.equals(isPaid)) {
            StatusUpdateRequest statusUpdate = StatusUpdateRequest.builder()
                    .status("PAIEMENT_VALIDE")
                    .commentaire("Paiement validé par le service financier")
                    .modifiePar("FINANCE")
                    .build();

            enrollmentClient.updateStatus(enrollmentId, statusUpdate);

            log.info("✅ Paiement validé pour demande: {}", enrollmentId);
        } else {
            log.warn("⚠️ Paiement non validé pour demande: {}", enrollmentId);
        }
    }
}