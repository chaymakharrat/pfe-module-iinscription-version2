package pfe.example.enrollement_module.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pfe.example.enrollement_module.dto.HistoriqueStatut.HistoriqueRequest;
import pfe.example.enrollement_module.dto.HistoriqueStatut.HistoriqueStatusDTO;
import pfe.example.enrollement_module.enumerateur.StatutDemandeInscription;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandeDetailDTO {
    private Long id;
    private String numeroDossier;
    private Long etudiantId;
    private String nomDiplome;
    private String langueDiplome;
    private StatutDemandeInscription statutActuel;
    private LocalDateTime dateCreation;
    private String processInstanceId;
    // Informations étudiant
    private EtudiantInfoDTO etudiant;

    // Documents soumis
    private List<DocumentStatusDTO> documents;

    // Historique des statuts
    private List<HistoriqueStatusDTO> historique;

    // Calculs
    private Double enAttenteDepuis; // en heures
    private String priorite; // HAUTE, MOYENNE, BASSE
}