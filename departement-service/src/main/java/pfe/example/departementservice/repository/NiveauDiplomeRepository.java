package pfe.example.departementservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pfe.example.departementservice.entities.NiveauDiplome;

public interface NiveauDiplomeRepository extends JpaRepository<NiveauDiplome,Long> {
    boolean existsByNiveau(int niveau);
}
