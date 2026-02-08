package pfe.example.departementservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;
@EqualsAndHashCode(of = {"nom"})
@Entity
@Data
public class Departement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;
    @OneToMany(mappedBy = "departement")
    private Set<DiplomeEtudier> diplomeEtudiers=new HashSet<>();
}
