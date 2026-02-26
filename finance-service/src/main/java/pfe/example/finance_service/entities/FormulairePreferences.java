package pfe.example.finance_service.entities;

import jakarta.persistence.*;
import lombok.*;
import pfe.example.finance_service.enumerateur.ModePaiement;
import pfe.example.finance_service.enumerateur.TypePaiement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormulairePreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long enrollmentId;

    @Enumerated(EnumType.STRING)
    private ModePaiement modePaiement;

    @Enumerated(EnumType.STRING)
    private TypePaiement typePaiement; // TOTAL, PARTIEL

    private Integer frequenceMois; // 1, 2, 3... null si TOTAL

    private String token; // sécuriser le lien formulaire

    private LocalDateTime dateReponse;

    private boolean reponseSoumise;

    // IDs des remises cochées par le candidat
    @ElementCollection
    private List<Long> remisesSelectionnees = new ArrayList<>();
}
