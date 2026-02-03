package pfe.example.departementservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Departement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;
    @Column(nullable = false, unique = true)
    @NotBlank(message = "L'email est obligatoire")
    private String email;
    @Column(nullable = false)
    @NotBlank(message = "Le téléphone est obligatoire")
    private String phone;
    @OneToMany(mappedBy = "departement")
    private Set<DiplomeEtudier> diplomeEtudiers=new HashSet<>();

}
