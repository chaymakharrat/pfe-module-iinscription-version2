package pfe.example.enrollement_module.dto.workflow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessStatusResponse {
    private String processInstanceId;
    private String currentActivity;
    private boolean isEnded;
    private String businessKey;
}
