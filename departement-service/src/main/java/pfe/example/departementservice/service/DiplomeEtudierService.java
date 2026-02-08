package pfe.example.departementservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pfe.example.departementservice.dto.DepartementDTO;
import pfe.example.departementservice.dto.DiplomeEtudierDTO;
import pfe.example.departementservice.dto.NiveauDiplomeSpecifiqueDTO;
import pfe.example.departementservice.entities.Departement;
import pfe.example.departementservice.entities.DiplomeEtudier;
import pfe.example.departementservice.entities.Niveau_diplome_specifique;
import pfe.example.departementservice.exception.BusinessException;
import pfe.example.departementservice.exception.ResourceNotFoundException;
import pfe.example.departementservice.mapper.DepartementMapper;
import pfe.example.departementservice.repository.DiplomeEtudierRepository;
import pfe.example.departementservice.repository.NiveauDiplomeSpecifiqueRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DiplomeEtudierService {
    private final DiplomeEtudierRepository diplomeEtudierRepository;
    private final DepartementService departementService;
    private final NiveauDiplomeSpecifiqueRepository niveauSpecifiqueRepository;
    private final DepartementMapper mapper;

    // ===== CRUD Diplôme =====

    public DiplomeEtudier createDiplome(DiplomeEtudier diplome) {
        log.info("Creating diplome: {}", diplome.getNom());

        if (diplomeEtudierRepository.existsByNom(diplome.getNom())) {
            throw new BusinessException("Un diplôme avec ce nom existe déjà");
        }

        // CORRECTION ICI : getDepartementById doit retourner Departement, pas DepartementDTO
        if (diplome.getDepartement() != null && diplome.getDepartement().getId() != null) {
            Departement departementEntity = departementService.getDepartementById(diplome.getDepartement().getId());
            diplome.setDepartement(departementEntity);
        }

        return diplomeEtudierRepository.save(diplome);
    }

    public DiplomeEtudier getDiplomeById(Long id) {
        log.info("Fetching diplome with id: {}", id);
        return diplomeEtudierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diplôme non trouvé avec l'ID: " + id));
    }

    public DiplomeEtudier getDiplomeByNom(String nom) {
        log.info("Fetching diplome with name: {}", nom);
        return diplomeEtudierRepository.findByNom(nom)
                .orElseThrow(() -> new ResourceNotFoundException("Diplôme non trouvé: " + nom));
    }

    public List<DiplomeEtudier> getAllDiplomes() {
        log.info("Fetching all diplomes");
        return diplomeEtudierRepository.findAll();
    }

    // NOUVELLE MÉTHODE : retourne des DTOs au lieu d'entités
    public List<DiplomeEtudierDTO> getAllDiplomesDto() {
        log.info("Fetching all diplomes as DTOs");
        return diplomeEtudierRepository.findAll().stream()
                .map(mapper::toDiplomeDto)
                .collect(Collectors.toList());
    }


    public List<DiplomeEtudier> getActiveDiplomes() {
        log.info("Fetching active diplomes");
        return diplomeEtudierRepository.findByActifTrue();
    }

    // CORRECTION ICI : getDepartementById retourne Departement
    public List<DiplomeEtudier> getDiplomesByDepartement(Long departementId) {
        log.info("Fetching diplomes for department: {}", departementId);
        Departement departement = departementService.getDepartementById(departementId);
        return diplomeEtudierRepository.findByDepartement(departement);
    }

    public DiplomeEtudier toggleDiplomeStatus(Long id) {
        log.info("Toggling diplome status for id: {}", id);
        DiplomeEtudier diplome = getDiplomeById(id);
        diplome.setActif(!diplome.isActif());
        return diplomeEtudierRepository.save(diplome);
    }

    public List<NiveauDiplomeSpecifiqueDTO> getNiveauxParDiplome(Long diplomeId) {
        log.info("Fetching niveaux for diplome id={}", diplomeId);
        DiplomeEtudier diplome = getDiplomeById(diplomeId);
        return  niveauSpecifiqueRepository.findByDiplome(diplome).stream()
                .map(mapper::toNiveauSpecifiqueDto)
                .collect(Collectors.toList());
    }

    public boolean canAcceptEnrollment(Long diplomeId, int currentEnrollmentCount) {
        DiplomeEtudier diplome = this.getDiplomeById(diplomeId);
        if (!diplome.isActif()) {
            log.warn("Diplome {} is not active", diplomeId);
            return false;
        }
        return true;
    }

    public double getFraisInscription(String nomDiplome) {
        DiplomeEtudier diplome = this.getDiplomeByNom(nomDiplome);
        return diplome.getFraisInscription();
    }
}