package com.example.workflow.delegate;// delegate/GenerateInvoiceDelegate.java
import com.example.workflow.client.EnrollmentServiceClient;
import com.example.workflow.client.FinanceServiceClient;
import com.example.workflow.dto.DemandeInscriptionDTO;
import com.example.workflow.dto.GenerateInvoiceRequest;
import com.example.workflow.dto.InvoiceDTO;
import com.example.workflow.dto.StatusUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("generateInvoiceDelegate")
@RequiredArgsConstructor
@Slf4j
public class GenerateInvoiceDelegate implements JavaDelegate {

    private final FinanceServiceClient financeClient;
    private final EnrollmentServiceClient enrollmentClient;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long enrollmentId = (Long) execution.getVariable("enrollmentId");
        Long studentId = (Long) execution.getVariable("studentId");
        String nomDiplome = (String) execution.getVariable("nomDiplome");

        log.info("💰 Génération de la facture pour la demande: {}", enrollmentId);

        // Récupérer les infos de l'inscription
        DemandeInscriptionDTO enrollment = enrollmentClient.getEnrollment(enrollmentId);

        // Générer la facture via Finance Service
        GenerateInvoiceRequest request = GenerateInvoiceRequest.builder()
                .enrollmentId(enrollmentId)
                .studentId(studentId)
                .nomDiplome(nomDiplome)
                .build();

        InvoiceDTO invoice = financeClient.generateInvoice(request);

        // Mettre à jour le statut
        StatusUpdateRequest statusUpdate = StatusUpdateRequest.builder()
                .status("FACTURE_GENEREE")
                .commentaire("Facture N°" + invoice.getNumero() + " générée - Montant: " + invoice.getMontantTotal() + " TND")
                .modifiePar("SYSTEM")
                .build();

        enrollmentClient.updateStatus(enrollmentId, statusUpdate);

        // Stocker l'ID de la facture dans le workflow
        execution.setVariable("invoiceId", invoice.getId());
        execution.setVariable("montantTotal", invoice.getMontantTotal());

        log.info("✅ Facture {} générée avec succès", invoice.getNumero());
    }
}