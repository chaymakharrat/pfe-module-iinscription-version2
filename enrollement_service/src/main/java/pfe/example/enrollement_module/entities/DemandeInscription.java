package pfe.example.enrollement_module.entities;


import lombok.*;
import pfe.example.enrollement_module.enumerateur.*;



import jakarta.persistence.*;



@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandeInscription {  // Correction orthographe
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    @Column(nullable = false)
//    private String nom;
//    @Column(nullable = false)
//    private String prenom;
//    @Column(nullable = false, unique = true)
//    private String email;

//    @ManyToOne(fetch = FetchType.LAZY)
//    private Pays pays;
//    @Column(nullable = false)
//    private String phone;

//    @Column(nullable = false)
//    private LocalDate dateNaissance;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private Genre genre;

//    @Enumerated(EnumType.STRING)
//    private Diplome dernierDiplome; // LICENCE, BACCALAUREA, MASTERE, INGENIEUR
//
//    private int anneeDernierDiplome;  // Ex: 2023
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "diplome_souhaite_id", nullable = false)
//    private DiplomeFaculté diplomeSouhaite; //licence/master
//
//    //Ajout de la relation avec les documents
//    @OneToMany(
//            mappedBy = "candidat",
//            cascade = CascadeType.ALL,
//            orphanRemoval = true,
//            fetch = FetchType.LAZY
//    )
//    private List<Document> documents = new ArrayList<>();
    //////
//    @Column(nullable = false)
//    private LocalDateTime dateCreation=LocalDateTime.now();
//    /////
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutDemandeInscription statut;
    ////////
//    @Column
//    private LocalDateTime dateValidationScolarite;
//
//    @Column
//    private LocalDateTime dateValidationDepartement;
//
//    @Column
//    private LocalDateTime dateAcceptation;  // Quand devient étudiant
//
//    @Column
//    private String validatedByScolarite;  // userId
//
//    @Column
//    private String validatedByDepartement;  // userId
//
//    @Column
//    private String rejectionReason;
    /// ///
//    private Long idEtudiant;
//    @Transient
//    private Etudiant etudiant;


//    // Méthodes utilitaires
//    public void addDocument(Document document) {
//        documents.add(document);
//        document.setCandidat(this);
//    }
//
//    public void removeDocument(Document document) {
//        documents.remove(document);
//        document.setCandidat(null);
//    }
//
//    // Vérifier si tous les documents obligatoires sont présents
//    public boolean hasAllRequiredDocuments() {
//        Set<TypeDocument> required = Set.of(
//                TypeDocument.CARTE_IDENTITE,
//                TypeDocument.DIPLOME_BAC,
//                TypeDocument.RELEVE_NOTES,
//                TypeDocument.PHOTO_IDENTITE,
//                TypeDocument.CERTIFICAT_NAISSANCE
//        );
//
//        Set<TypeDocument> present = documents.stream()
//                .map(Document::getType)
//                .collect(Collectors.toSet());
//
//        return present.containsAll(required);
//    }
}