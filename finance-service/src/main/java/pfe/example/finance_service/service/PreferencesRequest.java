package pfe.example.finance_service.service;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreferencesRequest {
    private String modePaiement;    // EN_LIGNE | EN_PRESENTIEL
    private String typePaiement;    // TOTAL | PARTIEL
    private Integer frequenceMois;  // null si TOTAL
    private List<Long> remisesSelectionnees;
}
