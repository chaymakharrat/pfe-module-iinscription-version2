package pfe.example.finance_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemandeInfoDTO {
    private Long id;
    private Long studentId;       // correspond à etudiantId
    private String nomDiplome;    // correspond à diplomeDemande
    private String langueDiplome;
    private String statut;
}
