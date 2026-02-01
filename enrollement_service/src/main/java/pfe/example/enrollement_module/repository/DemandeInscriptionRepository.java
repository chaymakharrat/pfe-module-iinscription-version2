package pfe.example.enrollement_module.repository;

import org.springframework.data.repository.CrudRepository;
import pfe.example.enrollement_module.entities.DemandeInscription;


public interface DemandeInscriptionRepository extends CrudRepository<DemandeInscription, Long> {
}
