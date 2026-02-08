package pfe.example.departementservice.dto;

import lombok.Data;

@Data
public class EnseignantDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String phone;
    private String diplomeNom; // pour éviter le cycle avec DiplomeEtudier
}
