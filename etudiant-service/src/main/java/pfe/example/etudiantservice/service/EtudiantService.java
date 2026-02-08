package pfe.example.etudiantservice.service;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pfe.example.etudiantservice.dto.PaysDTO;
import pfe.example.etudiantservice.entities.Etudiant;
import pfe.example.etudiantservice.entities.Pays;
import pfe.example.etudiantservice.enumerateur.StatutEtudiant;
import pfe.example.etudiantservice.exception.ResourceNotFoundException;
import pfe.example.etudiantservice.mapper.EtudiantMapper;
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

    /**
     * Créer un nouvel étudiant
     */
    public Etudiant createEtudiant(Etudiant etudiant) {
        log.info("Création d'un nouvel étudiant: {}", etudiant.getEmail());

        if (etudiantRepository.existsByEmail(etudiant.getEmail())) {
            throw new IllegalArgumentException("Un étudiant avec cet email existe déjà");
        }

        // Ne pas générer matricule ni dateInscription
        etudiant.setMatricule(null);
        etudiant.setDateInscription(null);
        etudiant.setStatut(StatutEtudiant.CANDIDAT);

        Etudiant savedEtudiant = etudiantRepository.save(etudiant);
        log.info("Étudiant créé avec succès (statut CANDIDAT) : {}", savedEtudiant.getEmail());

        return savedEtudiant;
    }
    /**
     * Récupérer tous les étudiants
     */
    public List<Etudiant> getAllEtudiants() {
        log.info("Fetching all students");
        return etudiantRepository.findAll();
    }

    public Etudiant updateEtudiant(Long id) {
        log.info("Updating student with id: {}", id);

        Etudiant etudiant = getEtudiantById(id);
        etudiant.setMatricule(generateMatricule());
        etudiant.setDateInscription(LocalDateTime.now());
        etudiant.setStatut(StatutEtudiant.INSCRIT);

        log.info("Étudiant inscrit : {} - matricule {}", etudiant.getNom(), etudiant.getMatricule());

        return etudiantRepository.save(etudiant);
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
     * pour le mement ne sont pas itule
     */
    /**
     * Récupérer les étudiants par pays
     */
    public List<Etudiant> getEtudiantsByPays(Long paysId) {
        log.info("Fetching students by country id: {}", paysId);
        Pays pays = paysRepository.findById(paysId)
                .orElseThrow(() -> new ResourceNotFoundException("Pays non trouvé"));
        return etudiantRepository.findByPays(pays);
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
    public Etudiant getEtudiantByNumCarteIdentite(String numCarteIdentite) {
        log.info("Fetching student with email: {}", numCarteIdentite);
        return etudiantRepository.findByNumCarteIdentite(numCarteIdentite)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé avec l'email: " + numCarteIdentite));
    }
    public Etudiant getEtudiantByNumPassportAndPays(String numPassport, Long paysId) {
        // Récupérer l'entité Pays depuis l'ID
        Pays pays = paysRepository.findById(paysId)
                .orElseThrow(() -> new ResourceNotFoundException("Pays non trouvé avec l'id : " + paysId));

        log.info("Fetching student with passport number: {} for country: {}", numPassport, pays.getNom());

        return etudiantRepository.findByNumPassportAndPays(numPassport, pays)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Étudiant non trouvé avec le numéro de passeport: " + numPassport + " pour le pays: " + pays.getNom()
                ));
    }


}