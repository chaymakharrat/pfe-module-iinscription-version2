package pfe.example.departementservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pfe.example.departementservice.entities.Departement;
import pfe.example.departementservice.entities.DiplomeEtudier;

import java.util.List;
import java.util.Optional;

public interface DiplomeEtudierRepository extends JpaRepository<DiplomeEtudier,Long> {
    Optional<DiplomeEtudier> findByNom(String nom);

    boolean existsByNom(String nom);

    List<DiplomeEtudier> findByActifTrue();

    List<DiplomeEtudier> findByDepartement(Departement departement);

}
