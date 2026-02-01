package com.example.camunda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

/**
 * DTO representing a Camunda task.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {
    
    /**
     * Task ID.
     */
    private String id;
    
    /**
     * Task name.
     */
    private String name;
    
    /**
     * Task assignee (user ID).
     */
    private String assignee;
    
    /**
     * Task creation date.
     */
    private Date created;
    
    /**
     * Task due date.
     */
    private Date due;
    
    /**
     * Process instance ID.
     */
    private String processInstanceId;
    
    /**
     * Process definition key.
     */
    private String processDefinitionKey;
    
    /**
     * Task description.
     */
    private String description;
    
    /**
     * Task variables.
     */
    private Map<String, Object> variables;
}
