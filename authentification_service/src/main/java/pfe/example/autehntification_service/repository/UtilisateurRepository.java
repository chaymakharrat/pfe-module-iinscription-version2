package pfe.example.autehntification_service.repository;

import org.springframework.data.repository.CrudRepository;
import pfe.example.autehntification_service.entities.Utilisateur;

import java.util.Optional;

public interface UtilisateurRepository extends CrudRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByLogin(String login);

    String login(String login);
}
