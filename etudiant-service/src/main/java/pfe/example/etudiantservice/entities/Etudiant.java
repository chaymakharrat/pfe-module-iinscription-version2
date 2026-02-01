package pfe.example.etudiantservice.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "etudiants")
@Data
public class Etudiant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ============================================
    // LIEN AVEC LA CANDIDATURE ORIGINALE
    // ============================================
    @Column(nullable = false, unique = true)
    private Long candidatId;  // FK vers enrollment_db.candidats

//    @Column(nullable = false, unique = true)
//    private Long userId;  // FK vers auth_db.users

    // ============================================
    // MATRICULE (généré lors de l'inscription)
    // ============================================
    @Column(nullable = false, unique = true)
    private String matricule;  // Ex: ITECH-2024-001

    // ============================================
    // DATES
    // ============================================
    @Column(nullable = false)
    private LocalDateTime dateInscription;

    @Column
    private LocalDateTime dateGraduation;

    // ============================================
    // INFORMATIONS COMPLÉMENTAIRES
    // ============================================
//    @Column
//    private String anneeScolaire;  // Ex: "2024-2025"
//
//    @Column
//    private Long programId;  // Programme dans lequel il est inscrit
}