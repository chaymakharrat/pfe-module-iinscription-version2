package pfe.example.departementservice.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class DiplomeEtudierDTO {
    private Long id;
    private String nom;
    private double fraisInscription;
    private boolean actif;
    private String departementNom;
    private String type;// juste le nom du departement
    //private Set<EnseignantDTO> enseignants; // optionnel si tu veux les enseignants
    private Set<String> prerequis=new HashSet<>(); //a partie du non de diplome et du type  je sait les prerequis
}
