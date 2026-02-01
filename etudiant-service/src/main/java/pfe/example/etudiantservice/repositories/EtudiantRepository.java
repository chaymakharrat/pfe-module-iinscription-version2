package pfe.example.etudiantservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pfe.example.etudiantservice.entities.Etudiant;

import java.util.Optional;

public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
    boolean existsByCandidatId(Long candidatId);
    Optional<Etudiant> findByCandidatId(Long candidatId);

}
