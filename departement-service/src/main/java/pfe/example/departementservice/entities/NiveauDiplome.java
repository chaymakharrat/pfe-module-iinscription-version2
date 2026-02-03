package pfe.example.departementservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.Set;
@Entity
@Data
public class NiveauDiplome {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    @Min(value = 1, message = "Le niveau doit être au moins 1")
    private int niveau;
}
