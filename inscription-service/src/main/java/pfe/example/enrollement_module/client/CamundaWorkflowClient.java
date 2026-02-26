// client/CamundaWorkflowClient.java
package pfe.example.enrollement_module.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import pfe.example.enrollement_module.dto.workflow.ProcessInstanceResponse;
import pfe.example.enrollement_module.dto.workflow.StartProcessRequest;


@FeignClient(
        name = "service-workflow",
        url = "${workflow.service.url}"
)
public interface CamundaWorkflowClient {

    @PostMapping("/api/process/enrollment/start")
    ProcessInstanceResponse startEnrollmentProcess(@RequestBody StartProcessRequest request);

    @GetMapping("/api/workflow/tasks/enrollment/{enrollmentId}")
    java.util.List<pfe.example.enrollement_module.dto.workflow.TaskDTO> getTasksByEnrollment(@PathVariable("enrollmentId") Long enrollmentId);

    @PostMapping("/api/workflow/tasks/{taskId}/complete")
    void completeTask(@PathVariable("taskId") String taskId, @RequestBody pfe.example.enrollement_module.dto.workflow.CompleteTaskRequest request);
}