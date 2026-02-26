package pfe.example.enrollement_module.dto.HistoriqueStatut; //// dto/StatusUpdateRequest.java


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusUpdateRequest {
    private String status;
    private String commentaire;
    private String loginUtilisateur;
    private LocalDateTime date;

}