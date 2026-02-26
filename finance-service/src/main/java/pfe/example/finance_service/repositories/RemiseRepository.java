package pfe.example.finance_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pfe.example.finance_service.entities.Remise;

import java.util.List;

public interface RemiseRepository extends JpaRepository<Remise, Long> {
    List<Remise> findByActifTrue(); // remises disponibles
}
