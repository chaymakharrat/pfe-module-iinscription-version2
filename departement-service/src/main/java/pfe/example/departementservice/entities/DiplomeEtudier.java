package pfe.example.departementservice.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiplomeEtudier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;
    @Min(value = 0, message = "Les frais ne peuvent pas être négatifs")
   private double fraisInscription;
   private boolean actif;
    @ManyToOne
    private Departement departement;
    @OneToMany(mappedBy = "diplomeEtudier")
    private Set<Enseignant> enseignants=new HashSet<>();
    @ManyToOne
    private Type type;
}
