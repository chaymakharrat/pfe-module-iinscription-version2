package pfe.example.notificationservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
public class Etudiant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @NotBlank(message = "Le nom est obligatoire")
    private String loginEnvoyeur;
    @Column(nullable = false)
    @NotBlank(message = "Le prénom est obligatoire")
    private String loginDestiataire;
    @Column(nullable = false)
    private String message;
    @Column(nullable = false)
    private LocalDate dateEnvoie;
    @ManyToOne
    @JoinColumn(nullable = false)
    private TypeNotification typeNptification;


}