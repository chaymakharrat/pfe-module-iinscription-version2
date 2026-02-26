package pfe.example.enrollement_module.dto.workflow;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessInstanceResponse {
    private String processInstanceId;
    private String processDefinitionKey;
    private boolean ended;
    private boolean suspended;
}
