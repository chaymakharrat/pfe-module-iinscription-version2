package pfe.example.finance_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pfe.example.finance_service.entities.Echeance;


import java.util.List;
import java.util.Optional;

public interface EcheanceRepository extends JpaRepository<Echeance, Long> {
    List<Echeance> findByFactureIdOrderByNumeroOrdreAsc(Long factureId);
    Optional<Echeance> findByFactureIdAndNumeroOrdre(Long factureId, int ordre);
}
