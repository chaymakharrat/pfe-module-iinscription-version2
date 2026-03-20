package pfe.example.enrollement_module;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_template")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ex: REJET_SCOLARITE, VALIDATION_PAIEMENT, LISTE_ATTENTE...
    @Column(unique = true, nullable = false, length = 60)
    private String code;

    // Rôle qui peut modifier ce template
    // AGENT_SCOLARITE | AGENT_FINANCE | ENSEIGNANT | SYSTEME
    @Column(nullable = false, length = 40)
    private String role;

    // Catégorie d'affichage dans l'UI
    // SCOLARITE | FINANCE | ENSEIGNANT | SYSTEME
    @Column(nullable = false, length = 30)
    private String categorie;

    // Label lisible pour l'interface
    @Column(nullable = false, length = 120)
    private String label;

    // Sujet de l'email — peut contenir {{variables}}
    @Column(nullable = false, length = 250)
    private String subject;

    // Corps HTML — peut contenir {{variables}}
    @Column(columnDefinition = "TEXT", nullable = false)
    private String bodyHtml;

    // JSON array des variables disponibles pour ce template
    // Ex: ["prenom","nom","motifRejet","nomDiplome"]
    @Column(columnDefinition = "TEXT")
    private String variablesDisponibles;

    // Description pour l'agent (quand cet email est envoyé ?)
    @Column(length = 300)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Boolean actif = true;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(length = 100)
    private String updatedBy;
}