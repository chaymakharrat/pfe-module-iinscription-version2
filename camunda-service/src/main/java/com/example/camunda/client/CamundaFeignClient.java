package com.example.camunda.client;

import com.example.camunda.dto.CompleteTaskRequest;
import com.example.camunda.dto.StartProcessRequest;
import com.example.camunda.dto.TaskDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Feign Client interface for Camunda Service.
 * 
 * Other microservices can use this interface to communicate with the Camunda service.
 * 
 * Usage in other microservices:
 * 1. Add this interface to your project
 * 2. Enable Feign clients with @EnableFeignClients
 * 3. Autowire this interface and use it like a regular service
 * 
 * Example:
 * @Autowired
 * private CamundaFeignClient camundaClient;
 * 
 * // Start a process
 * StartProcessRequest request = new StartProcessRequest();
 * request.setProcessDefinitionKey("myProcess");
 * request.setVariables(variables);
 * ResponseEntity<Map<String, Object>> response = camundaClient.startProcess(request);
 */
@FeignClient(name = "camunda-service", url = "${camunda.service.url:http://localhost:8085}")
public interface CamundaFeignClient {

    /**
     * Start a new process instance.
     * 
     * @param request StartProcessRequest containing process definition key and variables
     * @return Process instance information
     */
    @PostMapping("/api/process/start")
    ResponseEntity<Map<String, Object>> startProcess(@RequestBody StartProcessRequest request);

    /**
     * Get all tasks assigned to a specific user.
     * 
     * @param userId User ID
     * @return List of tasks
     */
    @GetMapping("/api/tasks/user/{userId}")
    ResponseEntity<List<TaskDto>> getUserTasks(@PathVariable("userId") String userId);

    /**
     * Get all tasks for a specific process instance.
     * 
     * @param processInstanceId Process instance ID
     * @return List of tasks
     */
    @GetMapping("/api/tasks/process/{processInstanceId}")
    ResponseEntity<List<TaskDto>> getProcessTasks(@PathVariable("processInstanceId") String processInstanceId);

    /**
     * Get all unassigned tasks.
     * 
     * @return List of unassigned tasks
     */
    @GetMapping("/api/tasks/unassigned")
    ResponseEntity<List<TaskDto>> getUnassignedTasks();

    /**
     * Complete a task.
     * 
     * @param taskId Task ID
     * @param request CompleteTaskRequest containing variables
     * @return Success status
     */
    @PostMapping("/api/tasks/{taskId}/complete")
    ResponseEntity<Map<String, Object>> completeTask(
            @PathVariable("taskId") String taskId,
            @RequestBody CompleteTaskRequest request);

    /**
     * Claim a task for a specific user.
     * 
     * @param taskId Task ID
     * @param userId User ID
     * @return Success status
     */
    @PostMapping("/api/tasks/{taskId}/claim/{userId}")
    ResponseEntity<Map<String, Object>> claimTask(
            @PathVariable("taskId") String taskId,
            @PathVariable("userId") String userId);
}
