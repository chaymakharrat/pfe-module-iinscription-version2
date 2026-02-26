package pfe.example.enrollement_module.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pfe.example.enrollement_module.entities.HistoriqueStatus;

import java.util.List;

public interface HistoriqueStatusRepository extends JpaRepository<HistoriqueStatus,Long> {
    List<HistoriqueStatus> findByDemandeInscriptionIdOrderByDateStatusDesc(Long demandeInscriptionId);
}
