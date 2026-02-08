package pfe.example.etudiantservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pfe.example.etudiantservice.entities.Pays;

import java.util.Optional;

public interface PaysRepository extends JpaRepository<Pays, Long>
{
    Optional<Pays> findByNom(String nom);
    boolean existsByNom(String nom);
    Optional<Pays> findByIndicatif(String indicatif);
}
