package pfe.example.enrollement_module.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pfe.example.enrollement_module.entities.DemandeInscription;
import pfe.example.enrollement_module.enumerateur.StatutDemandeInscription;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface DemandeInscriptionRepository extends JpaRepository<DemandeInscription, Long> {
    long countByStatutActuel(StatutDemandeInscription statut);

    Page<DemandeInscription> findByStatutActuel(StatutDemandeInscription statut, Pageable pageable);

    @Query("SELECT d.statutActuel as status, COUNT(d) as count FROM DemandeInscription d GROUP BY d.statutActuel")
    List<Object[]> countByStatus();

    java.util.Optional<DemandeInscription> findByEtudiantId(Long etudiantId);
    // DemandeInscriptionRepository.java
    Optional<DemandeInscription> findByTokenAcces(String token);
    List<DemandeInscription> findByNomDiplomeAndLangueDiplome(
            String nomDiplome, String langueDiplome);
    List<DemandeInscription> findByNomDiplome(String nomDiplome);
}
