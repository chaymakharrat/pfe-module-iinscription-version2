package pfe.example.etudiantservice.model;


import lombok.Data;

import java.time.LocalDate;

@Data
public class DemandeInscription {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
}
