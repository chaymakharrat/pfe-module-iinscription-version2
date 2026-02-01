package pfe.example.enrollement_module.entities;



import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


import java.util.List;


@Getter
@Setter
@Entity
@EqualsAndHashCode(of={"nom"})
public class Pays {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false,unique = true)
    private String nom;
    @Column(nullable = false, unique = true)
    private String indicatif;
    @OneToMany(mappedBy="pays")
    private List<DemandeInscription> candidats;
}