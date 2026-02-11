// client/CamundaWorkflowClient.java
package pfe.example.enrollement_module.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import pfe.example.enrollement_module.dto.*;


@FeignClient(
        name = "service-workflow",
        url = "${workflow.service.url}"
)
public interface CamundaWorkflowClient {

    @PostMapping("/api/process/enrollment/start")
    ProcessInstanceResponse startEnrollmentProcess(@RequestBody StartProcessRequest request);
}