package pfe.example.autehntification_service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import pfe.example.autehntification_service.entities.Role;
import pfe.example.autehntification_service.entities.Utilisateur;
import pfe.example.autehntification_service.repository.UtilisateurRepository;

@SpringBootApplication
@RequiredArgsConstructor // Lombok pour générer un constructeur avec final fields
public class AutehntificationServiceApplication implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UtilisateurRepository utilisateurRepository;

    public static void main(String[] args) {
        SpringApplication.run(AutehntificationServiceApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // Vérifie si l'admin existe déjà
        if (utilisateurRepository.findByLogin("admin").isEmpty()) {
            Utilisateur admin = new Utilisateur();
            admin.setLogin("admin");
            admin.setPassword(passwordEncoder.encode("admin123")); // 🔐 hash
            admin.setRole(Role.AGENT_SCOLARITE);
            utilisateurRepository.save(admin);
            System.out.println("Compte admin créé !");
        }
    }
}
