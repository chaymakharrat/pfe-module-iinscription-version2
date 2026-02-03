package pfe.example.departementservice.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pfe.example.departementservice.entities.Departement;
import pfe.example.departementservice.entities.DiplomeEtudier;
import pfe.example.departementservice.entities.NiveauDiplome;
import pfe.example.departementservice.service.DepartementService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/departements")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DepartementController {

    private final DepartementService departementService;

    // ===== Endpoints Département =====

    @PostMapping
    public ResponseEntity<Departement> createDepartement(@Valid @RequestBody Departement departement) {
        log.info("POST /api/departements - Creating department");
        Departement saved = departementService.createDepartement(departement);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Departement> getDepartementById(@PathVariable Long id) {
        log.info("GET /api/departements/{}", id);

        Departement departement = departementService.getDepartementById(id);
        return ResponseEntity.ok(departement);
    }

    @GetMapping("/nom/{nom}")
    public ResponseEntity<Departement> getDepartementByNom(@PathVariable String nom) {
        log.info("GET /api/departements/nom/{}", nom);

        Departement departement = departementService.getDepartementByNom(nom);
        return ResponseEntity.ok(departement);
    }

    @GetMapping
    public ResponseEntity<List<Departement>> getAllDepartements() {
        log.info("GET /api/departements - Fetching all departments");

        List<Departement> departements = departementService.getAllDepartements();
        List<Departement> s = departements.stream().collect(Collectors.toList());
        return ResponseEntity.ok(s);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Departement> updateDepartement(
            @PathVariable Long id,
            @Valid @RequestBody Departement departement) {
        log.info("PUT /api/departements/{}", id);
        Departement updated = departementService.updateDepartement(id, departement);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartement(@PathVariable Long id) {
        log.info("DELETE /api/departements/{}", id);

        departementService.deleteDepartement(id);
        return ResponseEntity.noContent().build();
    }

    // ===== Endpoints Diplôme =====

    @PostMapping("/diplomes")
    public ResponseEntity<DiplomeEtudier> createDiplome(@Valid @RequestBody DiplomeEtudier diplome) {
        log.info("POST /api/departements/diplomes - Creating diplome");
        DiplomeEtudier saved = departementService.createDiplome(diplome);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saved);
    }

    @GetMapping("/diplomes/{id}")
    public ResponseEntity<DiplomeEtudier> getDiplomeById(@PathVariable Long id) {
        log.info("GET /api/departements/diplomes/{}", id);
        DiplomeEtudier diplome = departementService.getDiplomeById(id);
        return ResponseEntity.ok(diplome);
    }

    @GetMapping("/diplomes/nom/{nom}")
    public ResponseEntity<DiplomeEtudier> getDiplomeByNom(@PathVariable String nom) {
        log.info("GET /api/departements/diplomes/nom/{}", nom);
        DiplomeEtudier diplome = departementService.getDiplomeByNom(nom);
        return ResponseEntity.ok(diplome);
    }

    @GetMapping("/diplomes")
    public ResponseEntity<List<DiplomeEtudier>> getAllDiplomes() {
        log.info("GET /api/departements/diplomes - Fetching all diplomes");

        List<DiplomeEtudier> diplomes = departementService.getAllDiplomes();
        List<DiplomeEtudier> s = diplomes.stream().collect(Collectors.toList());
        return ResponseEntity.ok(s);
    }

    @GetMapping("/diplomes/actifs")
    public ResponseEntity<List<DiplomeEtudier>> getActiveDiplomes() {
        log.info("GET /api/departements/diplomes/actifs");

        List<DiplomeEtudier> diplomes = departementService.getActiveDiplomes();
        List<DiplomeEtudier> s = diplomes.stream().collect(Collectors.toList());

        return ResponseEntity.ok(s);
    }

    @GetMapping("/{departementId}/diplomes")
    public ResponseEntity<List<DiplomeEtudier>> getDiplomesByDepartement(@PathVariable Long departementId) {
        log.info("GET /api/departements/{}/diplomes", departementId);

        List<DiplomeEtudier> diplomes = departementService.getDiplomesByDepartement(departementId);
        List<DiplomeEtudier> s = diplomes.stream().collect(Collectors.toList());
        return ResponseEntity.ok(s);
    }

    @PutMapping("/diplomes/{id}")
    public ResponseEntity<DiplomeEtudier> updateDiplome(
            @PathVariable Long id,
            @Valid @RequestBody DiplomeEtudier diplome) {

        log.info("PUT /api/departements/diplomes/{}", id);
        DiplomeEtudier updated = departementService.updateDiplome(id, diplome);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/diplomes/{id}")
    public ResponseEntity<Void> deleteDiplome(@PathVariable Long id) {
        log.info("DELETE /api/departements/diplomes/{}", id);

        departementService.deleteDiplome(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/diplomes/{id}/toggle-status")
    public ResponseEntity<DiplomeEtudier> toggleDiplomeStatus(@PathVariable Long id) {
        log.info("PATCH /api/departements/diplomes/{}/toggle-status", id);

        DiplomeEtudier diplome = departementService.toggleDiplomeStatus(id);
        return ResponseEntity.ok(diplome);
    }

    // ===== Endpoints Niveaux =====

    @PostMapping("/diplomes/{diplomeId}/niveaux/{niveauId}")
    public ResponseEntity<DiplomeEtudier> addNiveauToDiplome(
            @PathVariable Long diplomeId,
            @PathVariable Long niveauId) {

        log.info("POST /api/departements/diplomes/{}/niveaux/{}", diplomeId, niveauId);
        DiplomeEtudier diplome = departementService.addNiveauToDiplome(diplomeId, niveauId);
        return ResponseEntity.ok(diplome);
    }

    @DeleteMapping("/diplomes/{diplomeId}/niveaux/{niveauId}")
    public ResponseEntity<DiplomeEtudier> removeNiveauFromDiplome(
            @PathVariable Long diplomeId,
            @PathVariable Long niveauId) {

        log.info("DELETE /api/departements/diplomes/{}/niveaux/{}", diplomeId, niveauId);

        DiplomeEtudier diplome = departementService.removeNiveauFromDiplome(diplomeId, niveauId);
        return ResponseEntity.ok(diplome);
    }

    @PostMapping("/niveaux")
    public ResponseEntity<NiveauDiplome> createNiveau(@Valid @RequestBody NiveauDiplome niveau) {
        log.info("POST /api/departements/niveaux");
        NiveauDiplome saved = departementService.createNiveau(niveau);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saved);
    }

    @GetMapping("/niveaux")
    public ResponseEntity<List<NiveauDiplome>> getAllNiveaux() {
        log.info("GET /api/departements/niveaux");

        List<NiveauDiplome> niveaux = departementService.getAllNiveaux();
        List<NiveauDiplome> s = niveaux.stream().collect(Collectors.toList());
        return ResponseEntity.ok(s);
    }

    // ===== Endpoints de validation métier =====

    @GetMapping("/diplomes/{diplomeId}/can-accept-enrollment")
    public ResponseEntity<Boolean> canAcceptEnrollment(
            @PathVariable Long diplomeId,
            @RequestParam int currentEnrollmentCount) {

        log.info("GET /api/departements/diplomes/{}/can-accept-enrollment", diplomeId);

        boolean canAccept = departementService.canAcceptEnrollment(diplomeId, currentEnrollmentCount);
        return ResponseEntity.ok(canAccept);
    }

    @GetMapping("/diplomes/nom/{nomDiplome}/frais")
    public ResponseEntity<Double> getFraisInscription(@PathVariable String nomDiplome) {
        log.info("GET /api/departements/diplomes/nom/{}/frais", nomDiplome);

        double frais = departementService.getFraisInscription(nomDiplome);
        return ResponseEntity.ok(frais);
    }
}