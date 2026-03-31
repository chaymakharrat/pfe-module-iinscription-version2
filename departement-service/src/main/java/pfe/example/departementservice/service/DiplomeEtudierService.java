package pfe.example.departementservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pfe.example.departementservice.dto.DepartementDTO;
import pfe.example.departementservice.dto.*;
import pfe.example.departementservice.entities.*;
import pfe.example.departementservice.exception.BusinessException;
import pfe.example.departementservice.exception.ResourceNotFoundException;
import pfe.example.departementservice.mapper.DepartementMapper;
import pfe.example.departementservice.repository.*;

import java.util.List;
import java.util.Optional;
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
    private final DiplomeResponsableRepository diplomeResponsableRepository;
    private final NiveauDiplomeRepository niveauDiplomeRepository;
    private final pfe.example.departementservice.repository.EnseignantRepository enseignantRepository;
    private final AcademyNotificationService academyNotificationService;
    private final pfe.example.departementservice.client.EnrollmentServiceClient enrollmentServiceClient;

    // ===== CRUD Diplôme =====

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

    // NOUVELLE MÉTHODE : retourne des DTOs au lieu d'entités, filtrées par année
    public List<DiplomeEtudierDTO> getAllDiplomesDto(String annee) {
        log.info("Fetching all diplomes as DTOs for year: {}", annee);
        if (annee == null || annee.isBlank()) {
            return diplomeEtudierRepository.findAll().stream()
                    .map(mapper::toDiplomeDto)
                    .collect(Collectors.toList());
        }
        return diplomeEtudierRepository.findByAnneeUniversitaire(annee).stream()
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
        return diplomeEtudierRepository.findByDiplomeResponsable_Departement(departement);
    }

//    public DiplomeEtudier toggleDiplomeStatus(Long id) {
//        log.info("Toggling diplome status for id: {}", id);
//        DiplomeEtudier diplome = getDiplomeById(id);
//        diplome.setActif(!diplome.isActif());
//        return diplomeEtudierRepository.save(diplome);
//    }

    public List<NiveauDiplomeSpecifiqueDTO> getNiveauxParDiplome(Long diplomeId) {
        log.info("Fetching niveaux for diplome id={}", diplomeId);
        return  niveauSpecifiqueRepository.findByDiplome(getDiplomeById(diplomeId)).stream()
                .filter(Niveau_diplome_specifique::isActif)
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
// Dans DiplomeEtudierService.java — ajouter :
public double getFraisInscription(String nomDiplome, String langue) {
    // ✅ Cherche via DiplomeResponsable.nomDiplome (pas DiplomeEtudier.nom)
    DiplomeEtudier diplome = diplomeEtudierRepository
            .findByDiplomeResponsable_NomDiplomeAndLangue(nomDiplome, Langue.valueOf(langue))
            .orElseThrow(() -> new ResourceNotFoundException(
                    "Diplôme non trouvé: " + nomDiplome + " / " + langue));
    return diplome.getFraisInscription();
}

/**
 * Retourne les DiplomeEtudierDTO filtrés par type et année,
 * dédupliqués par nomDiplome.
 */
public List<DiplomeEtudierDTO> getDiplomesByType(String typeName, String annee) {
    log.info("Fetching diplomes for type={} year={}", typeName, annee);
    return diplomeEtudierRepository.findAll().stream()
            .filter(d -> d.getDiplomeResponsable() != null
                    && d.getDiplomeResponsable().getType() != null
                    && typeName.equalsIgnoreCase(d.getDiplomeResponsable().getType().getNom())
                    && d.isActif()
                    && (annee == null || annee.equalsIgnoreCase(d.getAnneeUniversitaire()))
                    && d.getDiplomeResponsable().isActif()
                    && d.getDiplomeResponsable().getDepartement() != null
                    && d.getDiplomeResponsable().getDepartement().isActif())
            .collect(Collectors.collectingAndThen(
                    Collectors.toMap(
                            DiplomeEtudier::getNom,
                            mapper::toDiplomeDto,
                            (a, b) -> a
                    ),
                    map -> List.copyOf(map.values())
            ));
}
public List<NiveauDiplomeSpecifiqueDTO> getNiveauxParDiplomeNomEtLangue(String nomDiplome, String langue) {
    log.info("Fetching niveaux for diplome name={} langue={}", nomDiplome, langue);

    DiplomeEtudier diplome = diplomeEtudierRepository
            .findByDiplomeResponsable_NomDiplomeAndLangue(nomDiplome, Langue.valueOf(langue))
            .orElseThrow(() -> new ResourceNotFoundException(
                    "Diplôme non trouvé: " + nomDiplome + " / " + langue));

    return niveauSpecifiqueRepository.findByDiplome(diplome).stream()
            .filter(Niveau_diplome_specifique::isActif)
            .map(mapper::toNiveauSpecifiqueDto)
            .collect(Collectors.toList());
}

/**
 * Crée une nouvelle variante (DiplomeEtudier) pour un DiplomeResponsable existant.
 */
public DiplomeEtudier createVariante(VarianteCreateRequest request) {
    log.info("Creating variante for diplome responsable id: {}", request.getDiplomeResponsableId());

    DiplomeResponsable resp = diplomeResponsableRepository.findById(request.getDiplomeResponsableId())
            .orElseThrow(() -> new ResourceNotFoundException("Diplôme responsable non trouvé: " + request.getDiplomeResponsableId()));

    if (!resp.isActif()) {
        throw new BusinessException("Impossible d'ajouter une variante à un diplôme responsable inactif");
    }

    DiplomeEtudier variante = DiplomeEtudier.builder()
            .nom(resp.getNomDiplome())
            .langue(Langue.valueOf(request.getLangue().toUpperCase()))
            .fraisInscription(request.getFraisInscription())
            .actif(request.isActif())
            .diplomeResponsable(resp)
            .build();

    variante = diplomeEtudierRepository.save(variante);

    // Créer les niveaux
    if (request.getNiveaux() != null) {
        for (NiveauDetailRequest ndr : request.getNiveaux()) {
            NiveauDiplome nd = (NiveauDiplome) niveauDiplomeRepository.findById(ndr.getNiveauId())
                    .orElseThrow(() -> new ResourceNotFoundException("Niveau non trouvé: " + ndr.getNiveauId()));

            Niveau_diplome_specifique nds = new Niveau_diplome_specifique();
            nds.setDiplome(variante);
            nds.setNiveau(nd);
            nds.setCapaciteMax(ndr.getCapaciteMax());
            nds.setTailleGroupe(ndr.getTailleGroupe());
            nds.setScoreMinimum(ndr.getScoreMinimum());
            nds.setActif(ndr.isActif());

            niveauSpecifiqueRepository.save(nds);
        }
    }

    variante = diplomeEtudierRepository.save(variante);

    // Notification
    academyNotificationService.notifyStakeholders(variante, AcademyEvent.VARIANTE_CREATION, null);

    return variante;
}

public DiplomeEtudier toggleDiplomeStatus(Long id) {
    log.info("Toggling diplome status for id: {}", id);
    DiplomeEtudier diplome = getDiplomeById(id);
    boolean newStatus = !diplome.isActif();

    // Si on désactive, on vérifie les inscriptions actives
    if (diplome.isActif() && !newStatus) {
        List<Long> niveauIds = niveauSpecifiqueRepository.findByDiplomeId(id).stream()
                .map(Niveau_diplome_specifique::getId)
                .collect(Collectors.toList());

        if (!niveauIds.isEmpty() && Boolean.TRUE.equals(enrollmentServiceClient.hasActiveEnrollments(niveauIds))) {
            throw new BusinessException("Impossible de désactiver cette variante : des demandes d'inscription sont en cours.");
        }
    }

    diplome.setActif(newStatus);
    DiplomeEtudier saved = diplomeEtudierRepository.save(diplome);

    academyNotificationService.notifyStakeholders(saved, AcademyEvent.VARIANTE_TOGGLE, null);
    return saved;
}

public DiplomeEtudier updateVariant(Long id, VarianteCreateRequest request, String senderEmail) {
    log.info("Updating variante id: {}", id);
    DiplomeEtudier variante = getDiplomeById(id);
    Double oldFees = variante.getFraisInscription();
    boolean feesChanged = !oldFees.equals(request.getFraisInscription());

    variante.setFraisInscription(request.getFraisInscription());
    variante.setActif(request.isActif());

    // Update existing levels or add new ones
    if (request.getNiveaux() != null) {
        for (NiveauDetailRequest ndr : request.getNiveaux()) {
            Optional<Niveau_diplome_specifique> existing = niveauSpecifiqueRepository
                    .findByDiplomeIdAndNiveauId(id, ndr.getNiveauId());

            if (existing.isPresent()) {
                Niveau_diplome_specifique ns = existing.get();

                // Règles d'intégrité sur la capacité et la taille du groupe
                Long waitlistCount = enrollmentServiceClient.countListeAttente(ns.getId());
                if (waitlistCount > 0) {
                    if (ndr.getCapaciteMax() < waitlistCount) {
                        throw new BusinessException("La capacité maximale (" + ndr.getCapaciteMax() + ") ne peut pas être inférieure au nombre d'étudiants en liste d'attente (" + waitlistCount + ") pour le niveau " + ns.getNiveau().getNiveau());
                    }
                    if (ndr.getTailleGroupe() != ns.getTailleGroupe()) {
                        throw new BusinessException("Impossible de modifier la taille du groupe pour le niveau " + ns.getNiveau().getNiveau() + " car des étudiants sont déjà en liste d'attente.");
                    }
                }

                ns.setCapaciteMax(ndr.getCapaciteMax());
                ns.setTailleGroupe(ndr.getTailleGroupe());
                ns.setScoreMinimum(ndr.getScoreMinimum());
                ns.setActif(ndr.isActif());
                niveauSpecifiqueRepository.save(ns);
            } else if (ndr.isActif()) {
                // Create if doesn't exist AND is marked as active in request
                NiveauDiplome nd = (NiveauDiplome) niveauDiplomeRepository.findById(ndr.getNiveauId())
                        .orElseThrow(() -> new ResourceNotFoundException("Niveau non trouvé: " + ndr.getNiveauId()));

                Niveau_diplome_specifique ns = new Niveau_diplome_specifique();
                ns.setDiplome(variante);
                ns.setNiveau(nd);
                ns.setCapaciteMax(ndr.getCapaciteMax());
                ns.setTailleGroupe(ndr.getTailleGroupe());
                ns.setScoreMinimum(ndr.getScoreMinimum());
                ns.setActif(true);
                niveauSpecifiqueRepository.save(ns);
            }
        }
    }

    DiplomeEtudier saved = diplomeEtudierRepository.save(variante);

    // Notification
    AcademyEvent event = feesChanged ? AcademyEvent.VARIANTE_UPDATE_FEES : AcademyEvent.VARIANTE_UPDATE_OTHER;
    academyNotificationService.notifyStakeholders(saved, event, senderEmail);

    return saved;
}
}
