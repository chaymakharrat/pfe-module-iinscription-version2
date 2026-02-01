package com.example.camunda.controller;

import com.example.camunda.dto.CompleteTaskRequest;
import com.example.camunda.dto.StartProcessRequest;
import com.example.camunda.dto.TaskDto;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for Camunda Process and Task operations.
 */
@RestController
@RequestMapping("/api")
public class ProcessController {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    /**
     * Start a new process instance.
     *
     * @param request StartProcessRequest containing process definition key and variables
     * @return Process instance ID and status
     */
    @PostMapping("/process/start")
    public ResponseEntity<Map<String, Object>> startProcess(@RequestBody StartProcessRequest request) {
        try {
            ProcessInstance processInstance;

            if (request.getBusinessKey() != null && !request.getBusinessKey().isEmpty()) {
                processInstance = runtimeService.startProcessInstanceByKey(
                    request.getProcessDefinitionKey(),
                    request.getBusinessKey(),
                    request.getVariables()
                );
            } else {
                processInstance = runtimeService.startProcessInstanceByKey(
                    request.getProcessDefinitionKey(),
                    request.getVariables()
                );
            }

            Map<String, Object> response = new HashMap<>();
            response.put("processInstanceId", processInstance.getProcessInstanceId());
            response.put("processDefinitionId", processInstance.getProcessDefinitionId());
            response.put("businessKey", processInstance.getBusinessKey());
            response.put("status", "STARTED");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get all tasks assigned to a specific user.
     *
     * @param userId User ID
     * @return List of tasks
     */
    @GetMapping("/tasks/user/{userId}")
    public ResponseEntity<List<TaskDto>> getUserTasks(@PathVariable String userId) {
        try {
            List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(userId)
                .list();

            List<TaskDto> taskDtos = tasks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

            return ResponseEntity.ok(taskDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all tasks for a specific process instance.
     *
     * @param processInstanceId Process instance ID
     * @return List of tasks
     */
    @GetMapping("/tasks/process/{processInstanceId}")
    public ResponseEntity<List<TaskDto>> getProcessTasks(@PathVariable String processInstanceId) {
        try {
            List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();

            List<TaskDto> taskDtos = tasks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

            return ResponseEntity.ok(taskDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all unassigned tasks (candidate tasks).
     *
     * @return List of unassigned tasks
     */
    @GetMapping("/tasks/unassigned")
    public ResponseEntity<List<TaskDto>> getUnassignedTasks() {
        try {
            List<Task> tasks = taskService.createTaskQuery()
                .taskUnassigned()
                .list();

            List<TaskDto> taskDtos = tasks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

            return ResponseEntity.ok(taskDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Complete a task.
     *
     * @param taskId Task ID
     * @param request CompleteTaskRequest containing variables
     * @return Success status
     */
    @PostMapping("/tasks/{taskId}/complete")
    public ResponseEntity<Map<String, Object>> completeTask(
            @PathVariable String taskId,
            @RequestBody(required = false) CompleteTaskRequest request) {
        try {
            Map<String, Object> variables = (request != null && request.getVariables() != null)
                ? request.getVariables()
                : new HashMap<>();

            taskService.complete(taskId, variables);

            Map<String, Object> response = new HashMap<>();
            response.put("taskId", taskId);
            response.put("status", "COMPLETED");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Claim a task for a specific user.
     *
     * @param taskId Task ID
     * @param userId User ID
     * @return Success status
     */
    @PostMapping("/tasks/{taskId}/claim/{userId}")
    public ResponseEntity<Map<String, Object>> claimTask(
            @PathVariable String taskId,
            @PathVariable String userId) {
        try {
            taskService.claim(taskId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("taskId", taskId);
            response.put("userId", userId);
            response.put("status", "CLAIMED");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Convert Camunda Task to TaskDto.
     */
    private TaskDto convertToDto(Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setName(task.getName());
        dto.setAssignee(task.getAssignee());
        dto.setCreated(task.getCreateTime());
        dto.setDue(task.getDueDate());
        dto.setProcessInstanceId(task.getProcessInstanceId());
        dto.setProcessDefinitionKey(task.getProcessDefinitionId());
        dto.setDescription(task.getDescription());

        // Get task variables
        Map<String, Object> variables = taskService.getVariables(task.getId());
        dto.setVariables(variables);

        return dto;
    }
}
