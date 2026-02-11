package pfe.example.etudiantservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import pfe.example.etudiantservice.enumerateur.Diplome;
import pfe.example.etudiantservice.enumerateur.Gendre;
import pfe.example.etudiantservice.enumerateur.StatutEtudiant;
import pfe.example.etudiantservice.enumerateur.TypeDocument;
import pfe.example.etudiantservice.validation.AgeMinimum;
import pfe.example.etudiantservice.validation.AnneeDiplomeValide;
import pfe.example.etudiantservice.validation.CinOrPassport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "detail_etudiants")
@Data
@CinOrPassport
@AnneeDiplomeValide
public class Etudiant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String matricule;  // Ex: ITECH-2024-001
    @Column(nullable = false)
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;
    @Column(nullable = false)
    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;
    @Column(nullable = false, unique = true)
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;
    @Column(nullable = false)
    private String phone;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Le gendre est obligatoire")
    private Gendre gendre;
    @Enumerated(EnumType.STRING)
    private Diplome dernierDiplome; // LICENCE, BACCALAUREA, MASTERE, INGENIEUR
    private int anneeDernierDiplome;  // Ex: 2023
    @Column(nullable = false)
    @AgeMinimum(value = 18, message = "L'étudiant doit avoir au moins 18 ans")
    private LocalDate dateNaissance;
    @Transient
    private int age;
    @Pattern(
            regexp = "^[0-9]{8}$",
            message = "Le numéro de carte d'identité doit contenir exactement 8 chiffres"
    )
    private String numCarteIdentite;
    private String numPassport;

    @ManyToOne(fetch = FetchType.LAZY)
    private Pays pays;
//    @Column(nullable = false)
    private LocalDateTime dateInscription;
    @OneToMany(
            mappedBy = "etudiant",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Document> documents = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutEtudiant statut=StatutEtudiant.CANDIDAT;



    // Méthodes utilitaires
    public void addDocument(Document document) {
        documents.add(document);
        document.setEtudiant(this);
    }

    public void removeDocument(Document document) {
        documents.remove(document);
        document.setEtudiant(null);
    }

    // Vérifier si tous les documents obligatoires sont présents
    public boolean hasAllRequiredDocuments() {
        Set<TypeDocument> required = Set.of(
                TypeDocument.CARTE_IDENTITE,
                TypeDocument.DIPLOME_BAC,
                TypeDocument.RELEVE_NOTES,
                //TypeDocument.PHOTO_IDENTITE,
                TypeDocument.CERTIFICAT_NAISSANCE
        );

        Set<TypeDocument> present = documents.stream()
                .map(Document::getType)
                .collect(Collectors.toSet());

        return present.containsAll(required);
    }


}