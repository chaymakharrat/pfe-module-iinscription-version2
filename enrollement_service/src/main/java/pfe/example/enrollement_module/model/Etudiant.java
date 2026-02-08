package pfe.example.enrollement_module.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Data
public class Etudiant {
    private String matricule;  // Ex: ITECH-2024-001
    private String nom;
    private String prenom;
    private String email;
    private Long idPays;
    private String phone;
    private LocalDate dateNaissance;
    private String genre;
    private String dernierDiplome;
    private int anneeDernierDiplome;  // Ex: 2023
    private List<String> documents = new ArrayList<>();
    private LocalDateTime dateInscription;
}
