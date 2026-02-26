package pfe.example.enrollement_module.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentStatusDTO {
    private Long documentId;
    private String type; // CARTE_IDENTITE, DIPLOME_BAC, etc.
    private String nomFichier;
    private String statut; // SOUMIS, MANQUANT, VALIDE, REJETE
    private Boolean isValidated;
    private String commentaireValidation;
}