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
    @Min(value = 1, message = "La capacité doit être au moins 1")
    private int capaciteMax;
    @Min(value = 0, message = "Les frais ne peuvent pas être négatifs")
   private double fraisInscription;
   private boolean actif;
   @ManyToMany
   @JoinTable(name = "diplome_niveau", joinColumns = @JoinColumn(name = "diplome_id"), inverseJoinColumns = @JoinColumn(name = "niveau_id"))
   private Set<NiveauDiplome> niveaux = new HashSet<>();
    @ManyToOne
    private Departement departement;


}
