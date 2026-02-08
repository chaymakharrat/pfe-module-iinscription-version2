package pfe.example.departementservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(of={"diplome","niveau"})
public class Niveau_diplome_specifique {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "diplome_id", nullable = false)
    private DiplomeEtudier diplome;

    @ManyToOne
    @JoinColumn(name = "niveau_id", nullable = false)
    private NiveauDiplome niveau;

    @Min(value = 1, message = "La capacité doit être au moins 1")
    private int capaciteMax;
}
