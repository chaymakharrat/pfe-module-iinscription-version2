package pfe.example.departementservice.dto;

import lombok.Data;

@Data
public class NiveauDiplomeSpecifiqueDTO {
    private Long id;
    private int capaciteMax;
    private int niveau;
    private String diplome; // optionnel si tu veux
}
