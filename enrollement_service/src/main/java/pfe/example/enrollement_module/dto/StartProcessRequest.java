package pfe.example.enrollement_module.dto;// model/StartProcessRequest.java


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartProcessRequest {
    private Long enrollmentId;
    private Long studentId;
    private String nomDiplome;
}