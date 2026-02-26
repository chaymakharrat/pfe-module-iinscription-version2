package pfe.example.enrollement_module.entities;

import jakarta.persistence.*;
import lombok.*;
import pfe.example.enrollement_module.enumerateur.StatutDemandeInscription;

import java.time.LocalDateTime;
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"demandeInscription","dateStatus","statut"})
public class HistoriqueStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatutDemandeInscription statut;
    @Column(nullable = false)
    private LocalDateTime dateStatus=LocalDateTime.now();
    @ManyToOne
    @JoinColumn(nullable = false)
    private DemandeInscription demandeInscription;
    @Column(columnDefinition = "TEXT")
    private String commentaire;
    private String loginUtilisateur;//apporter selon l'itulisatue qui a connecter et chnager la status
}
