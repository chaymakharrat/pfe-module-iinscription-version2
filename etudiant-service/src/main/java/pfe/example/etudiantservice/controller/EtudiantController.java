package pfe.example.etudiantservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pfe.example.etudiantservice.dto.EtudiantDTO;
import pfe.example.etudiantservice.entities.Document;
import pfe.example.etudiantservice.entities.Etudiant;

import pfe.example.etudiantservice.mapper.EtudiantMapper;

import jakarta.validation.Valid;
import pfe.example.etudiantservice.mapper.EtudiantRequestDTO;
import pfe.example.etudiantservice.service.EtudiantService;
import pfe.example.etudiantservice.service.JwtTokenProvider;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/etudiants")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class EtudiantController {

    private final EtudiantService etudiantService;
    private final EtudiantMapper etudiantMapper;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Créer un nouvel étudiant
     */
    @PostMapping
    public ResponseEntity<EtudiantDTO> createEtudiant(@Valid @RequestBody EtudiantRequestDTO requestDTO) {
        log.info("POST /api/etudiants - Creating new student");

        Etudiant etudiant = etudiantMapper.toEntity(requestDTO);
        Etudiant savedEtudiant = etudiantService.createEtudiant(etudiant);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(etudiantMapper.toDTO(savedEtudiant));
    }

    /**
     * Récupérer un étudiant par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<EtudiantDTO> getEtudiantById(@PathVariable Long id) {
        log.info("GET /api/etudiants/{} - Fetching student", id);

        Etudiant etudiant = etudiantService.getEtudiantById(id);
        return ResponseEntity.ok(etudiantMapper.toDTO(etudiant));
    }

    /**
     * Récupérer un étudiant par matricule
     */
    @GetMapping("/matricule/{matricule}")
    public ResponseEntity<EtudiantDTO> getEtudiantByMatricule(@PathVariable String matricule) {
        log.info("GET /api/etudiants/matricule/{} - Fetching student", matricule);

        Etudiant etudiant = etudiantService.getEtudiantByMatricule(matricule);
        return ResponseEntity.ok(etudiantMapper.toDTO(etudiant));
    }

    /**
     * Récupérer un étudiant par email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<EtudiantDTO> getEtudiantByEmail(@PathVariable String email) {
        log.info("GET /api/etudiants/email/{} - Fetching student", email);

        Etudiant etudiant = etudiantService.getEtudiantByEmail(email);
        return ResponseEntity.ok(etudiantMapper.toDTO(etudiant));
    }

    /**
     * Récupérer tous les étudiants
     */
    @GetMapping
    public ResponseEntity<List<EtudiantDTO>> getAllEtudiants() {
        log.info("GET /api/etudiants - Fetching all students");

        List<Etudiant> etudiants = etudiantService.getAllEtudiants();
        List<EtudiantDTO> dtos = etudiants.stream()
                .map(etudiantMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * Rechercher des étudiants
     */
    @GetMapping("/search")
    public ResponseEntity<List<EtudiantDTO>> searchEtudiants(@RequestParam String keyword) {
        log.info("GET /api/etudiants/search?keyword={}", keyword);

        List<Etudiant> etudiants = etudiantService.searchEtudiants(keyword);
        List<EtudiantDTO> dtos = etudiants.stream()
                .map(etudiantMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * Récupérer les étudiants par pays
     */
    @GetMapping("/pays/{paysId}")
    public ResponseEntity<List<EtudiantDTO>> getEtudiantsByPays(@PathVariable Long paysId) {
        log.info("GET /api/etudiants/pays/{}", paysId);

        List<Etudiant> etudiants = etudiantService.getEtudiantsByPays(paysId);
        List<EtudiantDTO> dtos = etudiants.stream()
                .map(etudiantMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * Mettre à jour un étudiant
     */
    @PutMapping("/{id}")
    public ResponseEntity<EtudiantDTO> updateEtudiant(
            @PathVariable Long id,
            @Valid @RequestBody EtudiantRequestDTO requestDTO) {

        log.info("PUT /api/etudiants/{} - Updating student", id);

        Etudiant etudiantDetails = etudiantMapper.toEntity(requestDTO);
        Etudiant updatedEtudiant = etudiantService.updateEtudiant(id, etudiantDetails);

        return ResponseEntity.ok(etudiantMapper.toDTO(updatedEtudiant));
    }

    /**
     * Supprimer un étudiant
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEtudiant(@PathVariable Long id) {
        log.info("DELETE /api/etudiants/{} - Deleting student", id);

        etudiantService.deleteEtudiant(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Vérifier si tous les documents requis sont présents
     */
    @GetMapping("/{id}/documents/check")
    public ResponseEntity<Boolean> checkRequiredDocuments(@PathVariable Long id) {
        log.info("GET /api/etudiants/{}/documents/check", id);

        boolean hasAll = etudiantService.hasAllRequiredDocuments(id);
        return ResponseEntity.ok(hasAll);
    }
    // 🎯 Récupère le profil de l'étudiant connecté
    @GetMapping("/me")
    public ResponseEntity<EtudiantDTO> getMyProfile(
            @RequestHeader("Authorization") String token) {
        // Extraire userId du JWT
        String userId = jwtTokenProvider.getUserIdFromToken(token);
        // Récupérer toutes les données de l'étudiant
        EtudiantDTO dashboard = etudiantService.getStudentDashboard(userId);
        return ResponseEntity.ok(dashboard);
    }
}