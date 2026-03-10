package pfe.example.enrollement_module.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pfe.example.enrollement_module.enumerateur.StatutDemandeInscription;
import pfe.example.enrollement_module.model.Etudiant;

import java.time.LocalDateTime;

/**
 * DTO léger pour l'API GET /api/demandes.
 * Évite de sérialiser l'entité JPA (et ses relations lazy) qui provoquait
 * ConcurrentModificationException lors de l'écriture JSON.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandeInscriptionDTO {
    private Long id;
    private LocalDateTime dateCreation;
    private Long studentId; // Pour la compatibilité
    private String diplomeDemande; // ⚠️ CHANGÉ : correspond au frontend
    private String langueDiplome;
    private String typeDiplome;
    private String niveauChoisi;
    private String processInstanceId;
    private StatutDemandeInscription statut; // ⚠️ CHANGÉ : correspond au frontend
    private EtudiantInfoDTO student; // ⚠️ AJOUTÉ : objet complet pour affichage
    private String tokenAcces;           // ✅ AJOUTER
    private LocalDateTime tokenExpiration;
}
