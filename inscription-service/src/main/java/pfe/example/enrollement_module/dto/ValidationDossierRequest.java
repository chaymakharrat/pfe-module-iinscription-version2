package pfe.example.enrollement_module.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO pour la validation ou le rejet d'un dossier par la scolarité
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationDossierRequest {

    @NotNull(message = "La décision est obligatoire")
    private String decision; // ACCEPTE ou REJETE

    private String commentaire;

    @NotBlank(message = "Le login utilisateur est obligatoire")
    private String loginUtilisateur;
}