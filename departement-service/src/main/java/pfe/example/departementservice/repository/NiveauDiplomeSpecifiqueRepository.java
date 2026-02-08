package pfe.example.departementservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pfe.example.departementservice.entities.DiplomeEtudier;
import pfe.example.departementservice.entities.Niveau_diplome_specifique;

import java.util.List;

public interface NiveauDiplomeSpecifiqueRepository extends JpaRepository<Niveau_diplome_specifique, Long> {
    List<Niveau_diplome_specifique> findByDiplome(DiplomeEtudier diplome);
}
