package pfe.example.enrollement_module.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EtudiantInfoDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String matricule;
    private String email;
    private String telephone;
    private LocalDate dateNaissance;
    private String numCarteIdentite;
    private String numPassport;
    private String paysNom;
    private String emailUniversitaire;
}