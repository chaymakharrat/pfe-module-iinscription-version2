package pfe.example.etudiantservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pfe.example.etudiantservice.entities.Document;
import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByEtudiantId(Long etudiantId);
}

