// dto/HistoriqueRequest.java
package pfe.example.enrollement_module.dto.HistoriqueStatut;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriqueRequest {
    private String nouveauStatus;
    private String commentaire;
    private String modifiePar;
    private LocalDateTime dateStatus;
}