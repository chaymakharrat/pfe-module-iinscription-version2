package pfe.example.etudiantservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pfe.example.etudiantservice.dto.PaysDTO;
import pfe.example.etudiantservice.entities.Etudiant;
import pfe.example.etudiantservice.entities.Pays;

import java.util.List;
import java.util.Optional;


public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
    Optional<Etudiant> findByMatricule(String matricule);

    Optional<Etudiant> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Etudiant> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);

    List<Etudiant> findByPays(Pays pays);
    Optional<Etudiant> findByNumCarteIdentite(String numCarteIdentite);
    Optional<Etudiant> findByNumPassportAndPays(String numPassport, Pays pays);






}
