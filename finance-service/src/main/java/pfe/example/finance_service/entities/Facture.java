package pfe.example.finance_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pfe.example.finance_service.enumerateur.ModePaiement;
import pfe.example.finance_service.enumerateur.StatusPaiement;
import pfe.example.finance_service.enumerateur.TypePaiement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Facture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateGeneration;
    private double montantTotal;
    private double montantPaye;
    private double montantRestant;
    private String numeroFacture;

    @Enumerated(EnumType.STRING)
    private StatusPaiement statusPaiement;

    @Enumerated(EnumType.STRING)
    private TypePaiement typePaiement;

    @Enumerated(EnumType.STRING)
    private ModePaiement modePaiement;

    private Integer frequenceMois;
    private Long enrollmentId;
    private LocalDate dateLimitePaiement;

    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL)
    private List<Remise> remises = new ArrayList<>();

    // Facture a 0 ou plusieurs échéances
    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL)
    private List<Echeance> echeances = new ArrayList<>();
}