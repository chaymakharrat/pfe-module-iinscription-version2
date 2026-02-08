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
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private StatutDemandeInscription statut;
    /// ////partie feign
    @Column(nullable = false)
    private Long etudiantId;  // ID de référence

    @Transient
    private Etudiant etudiant;  // Peuplé via Feign

    @Column(nullable = false)
    private String nomDiplome;

    @Transient
    private DiplomeAEtudier diplomeAEtudier;
    private String processInstanceId;
    ////////
    @OneToMany(mappedBy = "demandeInscription")
    private Set<HistoriqueStatus> historiqueStatus=new HashSet<>();

    /// ///
    /// ///
//    @Column
//    private LocalDateTime dateValidationScolarite;
//    @Column
//    private LocalDateTime dateValidationDepartement;// Quand devient étudiant
//    @Column
//    private LocalDateTime dateValidationFinance;
//    /// //donc lier au service authnetification validator
//    @Column
//    private String validatedByScolarite;  // userId
//    @Column
//    private String validatedByDepartement;  // userId
//    @Column private String validatedByFinance;


}//ba3d normalement fel status mech nzid attribut agent_faculte