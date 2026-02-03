package pfe.example.etudiantservice.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NiveauDiplome {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private int niveau;
    @OneToMany
    private Set<DiplomeFaculté> diplomeFacultés;
}
