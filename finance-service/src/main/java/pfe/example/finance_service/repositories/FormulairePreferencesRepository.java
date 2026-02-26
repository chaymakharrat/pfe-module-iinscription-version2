package pfe.example.finance_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pfe.example.finance_service.entities.FormulairePreferences;

import java.util.Optional;

public interface FormulairePreferencesRepository extends JpaRepository<FormulairePreferences, Long> {
    Optional<FormulairePreferences> findByEnrollmentId(Long enrollmentId);
    Optional<FormulairePreferences> findByToken(String token);
}
