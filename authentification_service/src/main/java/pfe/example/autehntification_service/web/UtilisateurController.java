package pfe.example.autehntification_service.web;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pfe.example.autehntification_service.dto.CreateUserRequest;
import pfe.example.autehntification_service.entities.Utilisateur;
import pfe.example.autehntification_service.exception.*;
import pfe.example.autehntification_service.repository.UtilisateurRepository;
import pfe.example.autehntification_service.service.KeycloakUserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/authentifier/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final KeycloakUserService keycloakUserService;
    private final UtilisateurRepository utilisateurRepository;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public Utilisateur createUtilisateur(@RequestBody CreateUserRequest request) {

        // 1️⃣ Créer dans Keycloak
        keycloakUserService.createUser(
                request.getLogin(),
                request.getPassword(),
                request.getRole()
        );

        // 2️⃣ Sauvegarder en base
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setLogin(request.getLogin());
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());

        return utilisateurRepository.save(utilisateur);
    }
}
