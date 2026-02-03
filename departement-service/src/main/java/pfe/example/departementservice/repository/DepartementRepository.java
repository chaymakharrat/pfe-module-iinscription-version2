package pfe.example.departementservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pfe.example.departementservice.entities.Departement;

import java.util.Optional;

public interface DepartementRepository extends JpaRepository<Departement,Long> {
    Optional<Departement> findByNom(String nom);

    boolean existsByNom(String nom);

    boolean existsByEmail(String email);
}
