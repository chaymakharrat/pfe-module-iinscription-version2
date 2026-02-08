package pfe.example.departementservice.dto;

import lombok.Data;
import java.util.Set;

@Data
public class DepartementDTO {
    private Long id;
    private String nom;
    private Set<DiplomeEtudierDTO> diplomes;
}
