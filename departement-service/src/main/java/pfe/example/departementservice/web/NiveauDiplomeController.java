package pfe.example.departementservice.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pfe.example.departementservice.entities.NiveauDiplome;
import pfe.example.departementservice.repository.NiveauDiplomeRepository;
import pfe.example.departementservice.service.NiveauDiplomeService;

import java.util.List;

@RestController
@RequestMapping("/api/niveaux")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Validated
public class NiveauDiplomeController {
    private final NiveauDiplomeService niveauDiplomeService;

    // ===== Endpoints Niveaux =====
    @PostMapping
    public ResponseEntity<NiveauDiplome> createNiveau(@Valid @RequestBody NiveauDiplome niveau) {
        log.info("POST /api/departements/niveaux");
        NiveauDiplome saved = niveauDiplomeService.createNiveau(niveau);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<NiveauDiplome>> getAllNiveaux() {
        log.info("GET /api/departements/niveaux");
        List<NiveauDiplome> niveaux = niveauDiplomeService.getAllNiveaux();
        return ResponseEntity.ok(niveaux);
    }
}
