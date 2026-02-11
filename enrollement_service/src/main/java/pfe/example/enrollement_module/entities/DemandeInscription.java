package pfe.example.enrollement_module.entities;


import lombok.*;
import pfe.example.enrollement_module.model.DiplomeAEtudier;
import pfe.example.enrollement_module.model.Etudiant;
import pfe.example.enrollement_module.enumerateur.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandeInscription {  // Correction orthographe
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDateTime dateCreation=LocalDateTime.now();
    @Column
    private String ReasonDeRejet;
    /// ////partie feign
    @Column(nullable = false)
    private Long etudiantId;  // ID de référence

    @Transient //gna yejem ikoun 3andha plusieurs etudiant
    private Etudiant etudiant;  // Peuplé via Feign

    @Column(nullable = false)
    private String nomDiplome;

    @Transient
    private DiplomeAEtudier diplomeAEtudier;
    private String processInstanceId;
    ////////
    @OneToMany(mappedBy = "demandeInscription")
    private Set<HistoriqueStatus> historiqueStatus=new HashSet<>();

}//ba3d normalement fel status mech nzid attribut agent_faculte