package pfe.example.enrollement_module.DTO;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Etudiant {
    private String matricule;  // Ex: ITECH-2024-001
    private String nom;
    private String prenom;
    private String email;
    private String pays;
    private String phone;
    private LocalDate dateNaissance;
    private String genre;
    private String dernierDiplome;
    private int anneeDernierDiplome;  // Ex: 2023
    private DiplomeFaculté diplomeSouhaite; //licence/master

    //Ajout de la relation avec les documents
    @OneToMany(
            mappedBy = "candidat",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Document> documents = new ArrayList<>();
    //////
    @Column(nullable = false)
    private LocalDateTime dateCreation=LocalDateTime.now();
    ////////
    @Column
    private LocalDateTime dateValidationScolarite;

    @Column
    private LocalDateTime dateValidationDepartement;

    @Column
    private LocalDateTime dateAcceptation;  // Quand devient étudiant

    @Column
    private String validatedByScolarite;  // userId

    @Column
    private String validatedByDepartement;  // userId

    @Column
    private String rejectionReason;
    @Column(nullable = false)
    private LocalDateTime dateInscription;
}
