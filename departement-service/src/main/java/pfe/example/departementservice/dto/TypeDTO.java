package pfe.example.departementservice.dto;

import lombok.Data;

import java.util.Set;

@Data
public class TypeDTO {
    private Long id;
    private String nom;
    private Set<String> prerequis;
}
