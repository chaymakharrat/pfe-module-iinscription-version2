package pfe.example.finance_service.entities;

import jakarta.persistence.*;
import lombok.*;
import pfe.example.finance_service.enumerateur.StatusEcheance;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Echeance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateEcheance;
    private double montantAPayer;
    private String numeroEcheance;
    private int numeroOrdre; // 1, 2, 3...

    @Enumerated(EnumType.STRING)
    private StatusEcheance statut; // EN_ATTENTE, PAYE, IMPAYE

    @ManyToOne
    @JoinColumn(name = "facture_id", nullable = false)
    private Facture facture;

    // Une échéance a 0 ou 1 paiement
    @OneToOne(mappedBy = "echeance", cascade = CascadeType.ALL)
    private Paiement paiement;
}
