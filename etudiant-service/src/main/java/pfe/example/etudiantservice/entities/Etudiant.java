package pfe.example.etudiantservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import pfe.example.etudiantservice.enumerateur.Diplome;
import pfe.example.etudiantservice.enumerateur.Genre;
import pfe.example.etudiantservice.enumerateur.StatutEtudiant;
import pfe.example.etudiantservice.enumerateur.TypeDocument;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "etudiants")
@Data
public class Etudiant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
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
    @NotNull(message = "Le genre est obligatoire")
    private Genre genre;
    // 🔗 LIEN AVEC AUTH-SERVICE (IMPORTANT)
    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;  // ID de l'utilisateur dans Auth-Service

    @Enumerated(EnumType.STRING)
    private Diplome dernierDiplome; // LICENCE, BACCALAUREA, MASTERE, INGENIEUR

    private int anneeDernierDiplome;  // Ex: 2023

    @Column(nullable = false)
    private LocalDate dateNaissance;

    @ManyToOne(fetch = FetchType.LAZY)
    private Pays pays;
    @Column(nullable = false)
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
                TypeDocument.PHOTO_IDENTITE,
                TypeDocument.CERTIFICAT_NAISSANCE
        );

        Set<TypeDocument> present = documents.stream()
                .map(Document::getType)
                .collect(Collectors.toSet());

        return present.containsAll(required);
    }


}