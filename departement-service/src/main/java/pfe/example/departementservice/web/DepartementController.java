package pfe.example.departementservice.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pfe.example.departementservice.dto.DepartementDTO;
import pfe.example.departementservice.entities.Departement;
import pfe.example.departementservice.entities.DiplomeEtudier;
import pfe.example.departementservice.entities.NiveauDiplome;
import pfe.example.departementservice.service.DepartementService;

import java.util.List;

@RestController
@RequestMapping("/api/departements")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Validated
public class DepartementController {

    private final DepartementService departementService;

    // ===== Endpoints Département =====

    @PostMapping
    public ResponseEntity<Departement> createDepartement(@Valid @RequestBody Departement departement) {
        log.info("POST /api/departements - Creating department");
        Departement saved = departementService.createDepartement(departement);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartementDTO> getDepartementById(@PathVariable @Min(1) Long id) {
        log.info("GET /api/departements/{}", id);
        DepartementDTO departement = departementService.getDepartementDtoById(id);
        return ResponseEntity.ok(departement);
    }

//    @GetMapping("/nom/{nom}")
//    public ResponseEntity<Departement> getDepartementByNom(@PathVariable String nom) {
//        log.info("GET /api/departements/nom/{}", nom);
//        Departement departement = departementService.getDepartementByNom(nom);
//        return ResponseEntity.ok(departement);
//    }

    @GetMapping
    public ResponseEntity<List<DepartementDTO>> getAllDepartements() {
        log.info("GET /api/departements - Fetching all departments");
        List<DepartementDTO> departements = departementService.getAllDepartementsDto();
        return ResponseEntity.ok(departements);
    }


}
