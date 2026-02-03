package pfe.example.etudiantservice.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pfe.example.etudiantservice.model.DemandeInscription;


import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiplomeFaculté {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String nom;
    @OneToMany
    private Set<NiveauDiplome> niveauDiplome;
    @OneToMany
    private Set<Etudiant> demandeInscription;

}
