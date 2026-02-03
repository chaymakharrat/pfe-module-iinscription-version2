package pfe.example.autehntification_service.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String login;
    @Column()
    private String password;   // stocker ici le hash du mot de passe

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}