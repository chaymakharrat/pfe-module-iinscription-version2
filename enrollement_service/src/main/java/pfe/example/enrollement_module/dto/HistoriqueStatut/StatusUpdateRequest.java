package pfe.example.enrollement_module.dto; //// dto/StatusUpdateRequest.java


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusUpdateRequest {
    private String status;
    private String commentaire;
    private String loginUtilisateur;

}