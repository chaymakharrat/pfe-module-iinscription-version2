package pfe.example.etudiantservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pfe.example.etudiantservice.entities.Pays;

public interface PaysRepository extends JpaRepository<Pays, Long>
{
}
