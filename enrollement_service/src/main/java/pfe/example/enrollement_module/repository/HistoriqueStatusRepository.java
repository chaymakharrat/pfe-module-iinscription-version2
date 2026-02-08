package pfe.example.enrollement_module.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pfe.example.enrollement_module.entities.HistoriqueStatus;

public interface HistoriqueStatusRepository extends JpaRepository<HistoriqueStatus,Long> {
}
