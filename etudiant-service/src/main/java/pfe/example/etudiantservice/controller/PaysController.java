package pfe.example.etudiantservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pfe.example.etudiantservice.dto.PaysDTO;
import pfe.example.etudiantservice.service.PaysService;

import java.util.List;

@RestController
@RequestMapping("/api/pays")
//@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PaysController {

    private final PaysService paysService;

    // GET /api/pays/noms
    @GetMapping("/noms")
    public List<String> getAllPaysNoms() {
        return paysService.getAllPaysNoms();
    }

    // GET /api/pays/noms-indicatifs
    @GetMapping("/noms-indicatifs")
    public List<PaysDTO> getAllPaysNomIndicatif() {
        return paysService.getAllPaysNomIndicatif();
    }
    @GetMapping("/indicatif/{indicatif}")
    public ResponseEntity<PaysDTO> getPaysByIndicatif(
            @PathVariable String indicatif) {
        PaysDTO paysDTO = paysService.getPaysByIndicatif(indicatif);
        return ResponseEntity.ok(paysDTO);
    }

}
