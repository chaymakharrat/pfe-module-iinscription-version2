package pfe.example.departementservice.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pfe.example.departementservice.dto.DiplomeEtudierDTO;
import pfe.example.departementservice.dto.NiveauDiplomeSpecifiqueDTO;
import pfe.example.departementservice.entities.DiplomeEtudier;
import pfe.example.departementservice.entities.Niveau_diplome_specifique;
import pfe.example.departementservice.mapper.DepartementMapper;
import pfe.example.departementservice.service.DiplomeEtudierService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/diplomes")
@RequiredArgsConstructor
@Slf4j
//@CrossOrigin(origins = "*")
@Validated
public class DiplomeEtudierController {
    private final DiplomeEtudierService diplomeEtudierService;
    private final DepartementMapper mapper; // Ajouter le mapper

    @PostMapping
    public ResponseEntity<DiplomeEtudierDTO> createDiplome(@Valid @RequestBody DiplomeEtudier diplome) {
        log.info("POST /api/diplomes - Creating diplome");
        DiplomeEtudier saved = diplomeEtudierService.createDiplome(diplome);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDiplomeDto(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiplomeEtudierDTO> getDiplomeById(@PathVariable @Min(1) Long id) {
        log.info("GET /api/diplomes/{}", id);
        DiplomeEtudier diplome = diplomeEtudierService.getDiplomeById(id);
        return ResponseEntity.ok(mapper.toDiplomeDto(diplome));
    }

    @GetMapping("/nom/{nom}")
    public ResponseEntity<DiplomeEtudierDTO> getDiplomeByNom(@PathVariable String nom) {
        log.info("GET /api/diplomes/nom/{}", nom);
        DiplomeEtudier diplome = diplomeEtudierService.getDiplomeByNom(nom);
        return ResponseEntity.ok(mapper.toDiplomeDto(diplome));
    }

    // CORRECTION PRINCIPALE : utiliser getAllDiplomesDto()
    @GetMapping
    public ResponseEntity<List<DiplomeEtudierDTO>> getAllDiplomes() {
        log.info("GET /api/diplomes - Fetching all diplomes");
        List<DiplomeEtudierDTO> diplomes = diplomeEtudierService.getAllDiplomesDto();
        return ResponseEntity.ok(diplomes);
    }

    @GetMapping("/actifs")
    public ResponseEntity<List<DiplomeEtudierDTO>> getActiveDiplomes() {
        log.info("GET /api/diplomes/actifs");
        List<DiplomeEtudier> diplomes = diplomeEtudierService.getActiveDiplomes();
        List<DiplomeEtudierDTO> dtos = diplomes.stream()
                .map(mapper::toDiplomeDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/departement/{departementId}")
    public ResponseEntity<List<DiplomeEtudierDTO>> getDiplomesByDepartement(@PathVariable @Min(1) Long departementId) {
        log.info("GET /api/diplomes/departement/{}", departementId);
        List<DiplomeEtudier> diplomes = diplomeEtudierService.getDiplomesByDepartement(departementId);
        List<DiplomeEtudierDTO> dtos = diplomes.stream()
                .map(mapper::toDiplomeDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}/niveaux")
    public ResponseEntity<List<NiveauDiplomeSpecifiqueDTO>> getNiveauxParDiplome(@PathVariable Long id) {
        List<NiveauDiplomeSpecifiqueDTO> niveaux = diplomeEtudierService.getNiveauxParDiplome(id);
        return ResponseEntity.ok(niveaux);
    }

    @GetMapping("/{diplomeId}/can-accept-enrollment")
    public ResponseEntity<Boolean> canAcceptEnrollment(
            @PathVariable @Min(1) Long diplomeId,
            @RequestParam @Min(0) int currentEnrollmentCount) {
        log.info("GET /api/diplomes/{}/can-accept-enrollment", diplomeId);
        boolean canAccept = diplomeEtudierService.canAcceptEnrollment(diplomeId, currentEnrollmentCount);
        return ResponseEntity.ok(canAccept);
    }

    @GetMapping("/nom/{nomDiplome}/frais")
    public ResponseEntity<Double> getFraisInscription(@PathVariable String nomDiplome) {
        log.info("GET /api/diplomes/nom/{}/frais", nomDiplome);
        double frais = diplomeEtudierService.getFraisInscription(nomDiplome);
        return ResponseEntity.ok(frais);
    }
}