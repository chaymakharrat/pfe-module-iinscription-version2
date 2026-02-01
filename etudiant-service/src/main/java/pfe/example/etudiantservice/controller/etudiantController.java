package pfe.example.etudiantservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pfe.example.etudiantservice.entities.Etudiant;
import pfe.example.etudiantservice.repositories.EtudiantRepository;
import pfe.example.etudiantservice.service.EtudiantService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/etudiants")
public class etudiantController {
    private final EtudiantService etudiantService;

    @PostMapping("/inscription/{candidatId}")
    public ResponseEntity<Etudiant> inscrireEtudiant(
            @PathVariable Long candidatId
    ) {
        Etudiant etudiant = etudiantService.inscrireEtudiant(candidatId);
        return ResponseEntity.ok(etudiant);
    }
    // Récupérer un étudiant par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Etudiant> getEtudiant(@PathVariable Long id) {
        Etudiant etudiant = etudiantService.getEtudiantById(id);
        return ResponseEntity.ok(etudiant);
    }

    // Récupérer un étudiant par le candidatId
    @GetMapping("/candidat/{candidatId}")
    public ResponseEntity<Etudiant> getEtudiantByCandidat(@PathVariable Long candidatId) {
        Etudiant etudiant = etudiantService.getEtudiantByCandidatId(candidatId);
        return ResponseEntity.ok(etudiant);
    }

}
