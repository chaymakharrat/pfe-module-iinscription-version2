package pfe.example.etudiantservice.service;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pfe.example.etudiantservice.client.AuthServiceClient;
import pfe.example.etudiantservice.dto.EtudiantDTO;
import pfe.example.etudiantservice.entities.Etudiant;
import pfe.example.etudiantservice.entities.Pays;
import pfe.example.etudiantservice.exception.ResourceNotFoundException;
import pfe.example.etudiantservice.repositories.EtudiantRepository;
import pfe.example.etudiantservice.repositories.PaysRepository;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;
    private final PaysRepository paysRepository;
    private final DocumentService documentStorageService;
    private final AuthServiceClient authServiceClient;

    /**
     * Créer un nouvel étudiant
     */
    public Etudiant createEtudiant(Etudiant etudiant) {
        log.info("Creating new student: {}", etudiant.getEmail());

        // Vérifier si l'email existe déjà
        if (etudiantRepository.existsByEmail(etudiant.getEmail())) {
            throw new IllegalArgumentException("Un étudiant avec cet email existe déjà");
        }

        // Générer le matricule
        etudiant.setMatricule(generateMatricule());
        etudiant.setDateInscription(LocalDateTime.now());

        Etudiant savedEtudiant = etudiantRepository.save(etudiant);
        log.info("Student created successfully with matricule: {}", savedEtudiant.getMatricule());

        return savedEtudiant;
    }

    /**
     * Récupérer un étudiant par ID
     */
    public Etudiant getEtudiantById(Long id) {
        log.info("Fetching student with id: {}", id);
        return etudiantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé avec l'ID: " + id));
    }

    /**
     * Récupérer un étudiant par matricule
     */
    public Etudiant getEtudiantByMatricule(String matricule) {
        log.info("Fetching student with matricule: {}", matricule);
        return etudiantRepository.findByMatricule(matricule)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé avec le matricule: " + matricule));
    }

    /**
     * Récupérer un étudiant par email
     */
    public Etudiant getEtudiantByEmail(String email) {
        log.info("Fetching student with email: {}", email);
        return etudiantRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé avec l'email: " + email));
    }

    /**
     * Récupérer tous les étudiants
     */
    public List<Etudiant> getAllEtudiants() {
        log.info("Fetching all students");
        return etudiantRepository.findAll();
    }

    /**
     * Mettre à jour un étudiant
     */
    public Etudiant updateEtudiant(Long id, Etudiant etudiantDetails) {
        log.info("Updating student with id: {}", id);

        Etudiant etudiant = getEtudiantById(id);

        // Mettre à jour les champs
        etudiant.setNom(etudiantDetails.getNom());
        etudiant.setPrenom(etudiantDetails.getPrenom());
        etudiant.setEmail(etudiantDetails.getEmail());
        etudiant.setPhone(etudiantDetails.getPhone());
        etudiant.setGenre(etudiantDetails.getGenre());
        etudiant.setDateNaissance(etudiantDetails.getDateNaissance());
        etudiant.setDernierDiplome(etudiantDetails.getDernierDiplome());
        etudiant.setAnneeDernierDiplome(etudiantDetails.getAnneeDernierDiplome());

        if (etudiantDetails.getPays() != null) {
            etudiant.setPays(etudiantDetails.getPays());
        }

        return etudiantRepository.save(etudiant);
    }

    /**
     * Supprimer un étudiant
     */
    public void deleteEtudiant(Long id) {
        log.info("Deleting student with id: {}", id);
        Etudiant etudiant = getEtudiantById(id);
        etudiantRepository.delete(etudiant);
    }
    /**
     * Vérifier si l'étudiant a tous les documents requis
     */
    public boolean hasAllRequiredDocuments(Long etudiantId) {
        Etudiant etudiant = getEtudiantById(etudiantId);
        return etudiant.hasAllRequiredDocuments();
    }

    /**
     * Générer un matricule unique
     */
    private String generateMatricule() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        long count = etudiantRepository.count() + 1;
        return String.format("ITECH-%s-%04d", year, count);
    }

    /**
     * Rechercher des étudiants par nom ou prénom
     */
    public List<Etudiant> searchEtudiants(String keyword) {
        log.info("Searching students with keyword: {}", keyword);
        return etudiantRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(keyword, keyword);
    }

    /**
     * Récupérer les étudiants par pays
     */
    public List<Etudiant> getEtudiantsByPays(Long paysId) {
        log.info("Fetching students by country id: {}", paysId);
        Pays pays = paysRepository.findById(paysId)
                .orElseThrow(() -> new ResourceNotFoundException("Pays non trouvé"));
        return etudiantRepository.findByPays(pays);
    }
    public EtudiantDTO getStudentDashboard(String userId) {
        // 1. Récupérer l'étudiant depuis la DB locale
        Etudiant etudiant = etudiantRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Étudiant non trouvé"));

        // 2. Construire le DTO avec toutes les infos
        return EtudiantDTO.builder()
                .id(etudiant.getId())
                .nom(etudiant.getNom())
                .prenom(etudiant.getPrenom())
                .email(etudiant.getEmail())
                .dateNaissance(etudiant.getDateNaissance())
                .build();
    }
}