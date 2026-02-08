package com.example.workflow.contoller;// controller/ProcessController.java
import com.example.workflow.dto.ProcessInstanceResponse;
import com.example.workflow.dto.ProcessStatusResponse;
import com.example.workflow.dto.StartProcessRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/process")
@RequiredArgsConstructor
@Slf4j
public class ProcessController {

    private final RuntimeService runtimeService;

    @PostMapping("/enrollment/start")
    public ResponseEntity<ProcessInstanceResponse> startEnrollmentProcess(
            @RequestBody StartProcessRequest request
    ) {
        log.info("🚀 Démarrage du processus d'inscription pour: {}", request.getEnrollmentId());

        // Préparer les variables du processus
        Map<String, Object> variables = new HashMap<>();
        variables.put("enrollmentId", request.getEnrollmentId());
        variables.put("studentId", request.getStudentId());
        variables.put("nomDiplome", request.getNomDiplome());

        // Démarrer le processus BPMN
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                "enrollment-process",  // Process ID du fichier BPMN
                request.getEnrollmentId().toString(), // Business Key
                variables
        );

        log.info("✅ Processus démarré avec ID: {}", processInstance.getId());

        // Créer la réponse
        ProcessInstanceResponse response = new ProcessInstanceResponse();
        response.setProcessInstanceId(processInstance.getId());
        response.setProcessDefinitionKey(processInstance.getProcessDefinitionId());
        response.setEnded(processInstance.isEnded());
        response.setSuspended(processInstance.isSuspended());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{processInstanceId}/status")
    public ResponseEntity<ProcessStatusResponse> getProcessStatus(
            @PathVariable String processInstanceId
    ) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        if (processInstance == null) {
            log.warn("⚠️ Processus non trouvé: {}", processInstanceId);
            return ResponseEntity.notFound().build();
        }

        ProcessStatusResponse response = new ProcessStatusResponse();
        response.setProcessInstanceId(processInstance.getId());
        response.setBusinessKey(processInstance.getBusinessKey());
        response.setEnded(processInstance.isEnded());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{processInstanceId}")
    public ResponseEntity<Void> cancelProcess(
            @PathVariable String processInstanceId
    ) {
        log.info("🗑️ Annulation du processus: {}", processInstanceId);
        runtimeService.deleteProcessInstance(processInstanceId, "Annulé par l'utilisateur");
        return ResponseEntity.noContent().build();
    }
}