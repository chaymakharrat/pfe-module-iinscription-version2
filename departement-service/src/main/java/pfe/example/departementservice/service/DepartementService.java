package pfe.example.departementservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pfe.example.departementservice.entities.*;
import pfe.example.departementservice.exception.*;
import pfe.example.departementservice.repository.*;


import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DepartementService {

    private final DepartementRepository departementRepository;
    private final DiplomeEtudierRepository diplomeEtudierRepository;
    private final NiveauDiplomeRepository niveauDiplomeRepository;

    // ===== CRUD Département =====

    /**
     * Créer un département
     */
    public Departement createDepartement(Departement departement) {
        log.info("Creating department: {}", departement.getNom());

        // Vérifier l'unicité du nom et de l'email
        if (departementRepository.existsByNom(departement.getNom())) {
            throw new BusinessException("Un département avec ce nom existe déjà");
        }

        if (departementRepository.existsByEmail(departement.getEmail())) {
            throw new BusinessException("Un département avec cet email existe déjà");
        }

        return departementRepository.save(departement);
    }

    /**
     * Récupérer un département par ID
     */
    public Departement getDepartementById(Long id) {
        log.info("Fetching department with id: {}", id);
        return departementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département non trouvé avec l'ID: " + id));
    }

    /**
     * Récupérer un département par nom
     */
    public Departement getDepartementByNom(String nom) {
        log.info("Fetching department with name: {}", nom);
        return departementRepository.findByNom(nom)
                .orElseThrow(() -> new ResourceNotFoundException("Département non trouvé: " + nom));
    }

    /**
     * Récupérer tous les départements
     */
    public List<Departement> getAllDepartements() {
        log.info("Fetching all departments");
        return departementRepository.findAll();
    }

    /**
     * Mettre à jour un département
     */
    public Departement updateDepartement(Long id, Departement departementDetails) {
        log.info("Updating department with id: {}", id);

        Departement departement = getDepartementById(id);

        departement.setNom(departementDetails.getNom());
        departement.setEmail(departementDetails.getEmail());
        departement.setPhone(departementDetails.getPhone());

        return departementRepository.save(departement);
    }

    /**
     * Supprimer un département
     */
    public void deleteDepartement(Long id) {
        log.info("Deleting department with id: {}", id);
        Departement departement = getDepartementById(id);
        departementRepository.delete(departement);
    }

    // ===== CRUD Diplôme à Étudier =====

    /**
     * Créer un diplôme
     */
    public DiplomeEtudier createDiplome(DiplomeEtudier diplome) {
        log.info("Creating diplome: {}", diplome.getNom());

        if (diplomeEtudierRepository.existsByNom(diplome.getNom())) {
            throw new BusinessException("Un diplôme avec ce nom existe déjà");
        }

        // Vérifier que le département existe
        if (diplome.getDepartement() != null && diplome.getDepartement().getId() != null) {
            Departement departement = getDepartementById(diplome.getDepartement().getId());
            diplome.setDepartement(departement);
        }

        return diplomeEtudierRepository.save(diplome);
    }

    /**
     * Récupérer un diplôme par ID
     */
    public DiplomeEtudier getDiplomeById(Long id) {
        log.info("Fetching diplome with id: {}", id);
        return diplomeEtudierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diplôme non trouvé avec l'ID: " + id));
    }

    /**
     * Récupérer un diplôme par nom
     */
    public DiplomeEtudier getDiplomeByNom(String nom) {
        log.info("Fetching diplome with name: {}", nom);
        return diplomeEtudierRepository.findByNom(nom)
                .orElseThrow(() -> new ResourceNotFoundException("Diplôme non trouvé: " + nom));
    }

    /**
     * Récupérer tous les diplômes
     */
    public List<DiplomeEtudier> getAllDiplomes() {
        log.info("Fetching all diplomes");
        return diplomeEtudierRepository.findAll();
    }

    /**
     * Récupérer les diplômes actifs
     */
    public List<DiplomeEtudier> getActiveDiplomes() {
        log.info("Fetching active diplomes");
        return diplomeEtudierRepository.findByActifTrue();
    }

    /**
     * Récupérer les diplômes par département
     */
    public List<DiplomeEtudier> getDiplomesByDepartement(Long departementId) {
        log.info("Fetching diplomes for department: {}", departementId);
        Departement departement = getDepartementById(departementId);
        return diplomeEtudierRepository.findByDepartement(departement);
    }

    /**
     * Mettre à jour un diplôme
     */
    public DiplomeEtudier updateDiplome(Long id, DiplomeEtudier diplomeDetails) {
        log.info("Updating diplome with id: {}", id);

        DiplomeEtudier diplome = getDiplomeById(id);

        diplome.setNom(diplomeDetails.getNom());
        diplome.setCapaciteMax(diplomeDetails.getCapaciteMax());
        diplome.setFraisInscription(diplomeDetails.getFraisInscription());
        diplome.setActif(diplomeDetails.isActif());

        if (diplomeDetails.getDepartement() != null) {
            diplome.setDepartement(diplomeDetails.getDepartement());
        }

        return diplomeEtudierRepository.save(diplome);
    }

    /**
     * Supprimer un diplôme
     */
    public void deleteDiplome(Long id) {
        log.info("Deleting diplome with id: {}", id);
        DiplomeEtudier diplome = getDiplomeById(id);
        diplomeEtudierRepository.delete(diplome);
    }

    /**
     * Activer/Désactiver un diplôme
     */
    public DiplomeEtudier toggleDiplomeStatus(Long id) {
        log.info("Toggling diplome status for id: {}", id);
        DiplomeEtudier diplome = getDiplomeById(id);
        diplome.setActif(!diplome.isActif());
        return diplomeEtudierRepository.save(diplome);
    }

    // ===== Gestion des niveaux =====

    /**
     * Ajouter un niveau à un diplôme
     */
    public DiplomeEtudier addNiveauToDiplome(Long diplomeId, Long niveauId) {
        log.info("Adding niveau {} to diplome {}", niveauId, diplomeId);

        DiplomeEtudier diplome = getDiplomeById(diplomeId);
        NiveauDiplome niveau = niveauDiplomeRepository.findById(niveauId)
                .orElseThrow(() -> new ResourceNotFoundException("Niveau non trouvé"));

        diplome.getNiveaux().add(niveau);
        return diplomeEtudierRepository.save(diplome);
    }

    /**
     * Retirer un niveau d'un diplôme
     */
    public DiplomeEtudier removeNiveauFromDiplome(Long diplomeId, Long niveauId) {
        log.info("Removing niveau {} from diplome {}", niveauId, diplomeId);

        DiplomeEtudier diplome = getDiplomeById(diplomeId);
        NiveauDiplome niveau = niveauDiplomeRepository.findById(niveauId)
                .orElseThrow(() -> new ResourceNotFoundException("Niveau non trouvé"));

        diplome.getNiveaux().remove(niveau);
        return diplomeEtudierRepository.save(diplome);
    }

    /**
     * Créer un niveau de diplôme
     */
    public NiveauDiplome createNiveau(NiveauDiplome niveau) {
        log.info("Creating niveau: {}", niveau.getNiveau());

        if (niveauDiplomeRepository.existsByNiveau(niveau.getNiveau())) {
            throw new BusinessException("Ce niveau existe déjà");
        }

        return niveauDiplomeRepository.save(niveau);
    }

    /**
     * Récupérer tous les niveaux
     */
    public List<NiveauDiplome> getAllNiveaux() {
        log.info("Fetching all niveaux");
        return niveauDiplomeRepository.findAll();
    }

    // ===== Méthodes de validation métier =====

    /**
     * Vérifier si un diplôme peut encore accepter des inscriptions
     */
    public boolean canAcceptEnrollment(Long diplomeId, int currentEnrollmentCount) {
        DiplomeEtudier diplome = getDiplomeById(diplomeId);

        if (!diplome.isActif()) {
            log.warn("Diplome {} is not active", diplomeId);
            return false;
        }

        if (currentEnrollmentCount >= diplome.getCapaciteMax()) {
            log.warn("Diplome {} has reached max capacity", diplomeId);
            return false;
        }

        return true;
    }

    /**
     * Récupérer les frais d'inscription pour un diplôme
     */
    public double getFraisInscription(String nomDiplome) {
        DiplomeEtudier diplome = getDiplomeByNom(nomDiplome);
        return diplome.getFraisInscription();
    }
}