package pfe.example.finance_service.entities;

import jakarta.persistence.*;
import lombok.*;
import pfe.example.finance_service.enumerateur.ModePaiement;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate datePaiement;
    private double montantAPayer;
    private String numeroPaiement;

    @Enumerated(EnumType.STRING)
    private ModePaiement modePaiement;

    // Paiement appartient à une échéance
    @OneToOne
    @JoinColumn(name = "echeance_id", nullable = false)
    private Echeance echeance;
}
