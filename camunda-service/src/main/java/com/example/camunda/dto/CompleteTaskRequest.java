package com.example.camunda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for completing a task.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompleteTaskRequest {
    
    /**
     * Variables to set when completing the task.
     */
    private Map<String, Object> variables;
}
