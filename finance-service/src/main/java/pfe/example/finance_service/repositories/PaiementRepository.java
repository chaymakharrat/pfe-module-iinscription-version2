package pfe.example.finance_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pfe.example.finance_service.entities.Paiement;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {}
