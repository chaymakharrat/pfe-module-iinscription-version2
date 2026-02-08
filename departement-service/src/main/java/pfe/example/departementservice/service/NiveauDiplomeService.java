package pfe.example.departementservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pfe.example.departementservice.entities.NiveauDiplome;
import pfe.example.departementservice.exception.BusinessException;
import pfe.example.departementservice.repository.NiveauDiplomeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NiveauDiplomeService {
    private final NiveauDiplomeRepository niveauDiplomeRepository;
    // ===== Gestion des niveaux =====

    public NiveauDiplome createNiveau(NiveauDiplome niveau) {
        log.info("Creating niveau: {}", niveau.getNiveau());

        if (niveauDiplomeRepository.existsByNiveau(niveau.getNiveau())) {
            throw new BusinessException("Ce niveau existe déjà");
        }

        return niveauDiplomeRepository.save(niveau);
    }

    public List<NiveauDiplome> getAllNiveaux() {
        log.info("Fetching all niveaux");
        return niveauDiplomeRepository.findAll();
    }
}
