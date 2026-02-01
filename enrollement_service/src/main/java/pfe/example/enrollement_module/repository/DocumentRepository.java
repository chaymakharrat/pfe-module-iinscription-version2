package pfe.example.enrollement_module.repository;

import org.springframework.data.repository.CrudRepository;
import pfe.example.enrollement_module.entities.Document;


import java.util.List;

public interface DocumentRepository extends CrudRepository<Document, Long> {
    List<Document> findByCandidatId(Long candidatId);
}
