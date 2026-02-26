package pfe.example.finance_service.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Remise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String motif; // "Frère inscrit", "Boursier", etc.

    @Column(nullable = false)
    private double pourcentage; // 10%, 20%, etc.

    private boolean actif;

    @ManyToOne
    @JoinColumn(name = "facture_id")
    private Facture facture;
}
