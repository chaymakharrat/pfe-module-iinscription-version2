package pfe.example.etudiantservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pfe.example.etudiantservice.enumerateur.Diplome;
import pfe.example.etudiantservice.enumerateur.Genre;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EtudiantDTO {

    private Long id;
    private String matricule;
    private String nom;
    private String prenom;
    private String email;
    private String phone;
    private Genre genre;
    private Diplome dernierDiplome;
    private int anneeDernierDiplome;
    private LocalDate dateNaissance;
    private String paysNom;
    private LocalDateTime dateInscription;
    private boolean hasAllRequiredDocuments;
    private String userId;
}