package pfe.example.finance_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pfe.example.finance_service.entities.Facture;

import java.util.Optional;

public interface FactureRepository extends JpaRepository<Facture, Long> {
    Optional<Facture> findByEnrollmentId(Long enrollmentId);
}
