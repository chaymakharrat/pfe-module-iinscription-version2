package pfe.example.autehntification_service.web;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pfe.example.autehntification_service.entities.Utilisateur;
import pfe.example.autehntification_service.exception.*;
import pfe.example.autehntification_service.repository.UtilisateurRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("authentifier/utilisateurs")
@CrossOrigin(origins = "*")
public class UtilisateurController {
    @Autowired
    private UtilisateurRepository UtilisateurRepository;

    @PostMapping("/")
    @PreAuthorize("hasAuthority('SCOPE_AGENT_SCOLARITE')")
    public Utilisateur createUtilisateur(@RequestBody Utilisateur utilisateur)
    {
        return UtilisateurRepository.save(utilisateur);
    }

    @GetMapping("/{id}")

    public ResponseEntity<Utilisateur> getUtilisateurBiId(@PathVariable Long id)
    {
        Utilisateur utilisateur = UtilisateurRepository.findById(id)
                .orElseThrow(() -> new RessourceNotExist("Utilisateur not exist with id :" + id));
        return ResponseEntity.ok(utilisateur);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_AGENT_SCOLARITE')")
    public ResponseEntity<Utilisateur> updateUtilisateur(@PathVariable Long id, @RequestBody Utilisateur utilisateurDetail)
    {
        Utilisateur utilisateur = UtilisateurRepository.findById(id)
                .orElseThrow(() -> new RessourceNotExist("Utilisateur not exist with id :" + id));
        utilisateur.setLogin(utilisateurDetail.getLogin());
        utilisateur.setRole(utilisateurDetail.getRole());
        Utilisateur updateUtilisateur=UtilisateurRepository.save(utilisateur);
        return ResponseEntity.ok(updateUtilisateur);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_AGENT_SCOLARITE')")
    public ResponseEntity<Map<String,Boolean>> deleteUtilisateur(@PathVariable Long id)
    {
        Utilisateur utilisateur = UtilisateurRepository.findById(id)
                .orElseThrow(() -> new RessourceNotExist("Utilisateur not exist with id :" + id));
        UtilisateurRepository.delete(utilisateur);
        Map<String,Boolean> response=new HashMap<>();
        response.put("deleted", true);
        return ResponseEntity.ok(response);
    }
}
