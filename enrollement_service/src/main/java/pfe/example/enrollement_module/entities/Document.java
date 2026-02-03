//package pfe.example.enrollement_module.entities;
//
//
//import jakarta.persistence.*;
//import lombok.*;
//import pfe.example.enrollement_module.enumerateur.TypeDocument;
//
//
//
//
//@Entity
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class Document {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private TypeDocument type;
//    //cin_chaima.pdf
//    @Column(nullable = false)
//    private String nomFichier;
//    //Nom réel du fichier stocké sur le serveur
//    @Column(nullable = false)
//    private String cheminFichier;  // ou URL si stockage cloud
//
//    private String contentType;  // image/jpeg, application/pdf, etc.
//    @Column(nullable = false)
//    private Boolean isValidated = false;
//
//    private Long tailleFichier;  // en bytes
//
//   //private LocalDateTime dateTelechargement;
//
//    private String commentaireValidation;  // Si rejeté, pourquoi ?
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "candidat_id", nullable = false)
//    private DemandeInscription candidat;
//}
