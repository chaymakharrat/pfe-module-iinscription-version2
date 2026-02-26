package pfe.example.finance_service.DTO;

import lombok.*;
import pfe.example.finance_service.enumerateur.ModePaiement;
import pfe.example.finance_service.enumerateur.TypePaiement;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormulairePreferencesPaiementDTO {
    private Long id;
    private Long enrollmentId;
    private String nomDiplome;          // enrichi depuis enrollment-service
    private String token;
    private boolean reponseSoumise;
    private ModePaiement modePaiement;
    private TypePaiement typePaiement;
    private Integer frequenceMois;
    private List<Long> remisesSelectionnees;
    private LocalDateTime dateExpiration; // dateCreation + 3 jours
}
