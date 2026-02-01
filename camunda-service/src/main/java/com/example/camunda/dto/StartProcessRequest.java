package com.example.camunda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for starting a new process instance.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartProcessRequest {
    
    /**
     * The key of the process definition to start (as defined in BPMN file).
     */
    private String processDefinitionKey;
    
    /**
     * Business key for the process instance (optional).
     */
    private String businessKey;
    
    /**
     * Process variables to pass when starting the process.
     */
    private Map<String, Object> variables;
}
