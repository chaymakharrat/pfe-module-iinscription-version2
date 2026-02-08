package pfe.example.etudiantservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaysApiDto {
    @JsonProperty("name_fr")
    private String nom;
    
    private String indicatif;
}
