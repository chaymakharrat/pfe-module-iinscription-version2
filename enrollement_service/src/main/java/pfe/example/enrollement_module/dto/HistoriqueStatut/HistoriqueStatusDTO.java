package pfe.example.enrollement_module.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour représenter un événement dans l'historique d'une demande
 * Affiche les changements de statut avec date et auteur
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueStatusDTO {
    private Long id;
    private String statut;
    private String commentaire;
    private String loginUtilisateur;
    private LocalDateTime dateStatus;
}