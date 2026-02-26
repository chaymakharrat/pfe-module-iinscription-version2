package pfe.example.enrollement_module.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pfe.example.enrollement_module.dto.*;
import pfe.example.enrollement_module.dto.HistoriqueStatut.StatusUpdateRequest;
import pfe.example.enrollement_module.services.DemandeInscriptionService;
import pfe.example.enrollement_module.services.ScolariteService;


import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/scolarite")
@RequiredArgsConstructor
@Slf4j
public class ScolariteController {

    private final ScolariteService scolariteService;
    private final DemandeInscriptionService demandeService;

    /**
     * Récupérer toutes les demandes avec détails - VERSION PAGINÉE
     */
    @GetMapping("/demandes")
    public ResponseEntity<Page<DemandeDetailDTO>> getAllDemandes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateCreation,desc") String[] sort
    ) {
        log.info("GET /api/scolarite/demandes - page: {}, size: {}", page, size);

        Pageable pageable = createPageable(page, size, sort);
        Page<DemandeDetailDTO> demandes = scolariteService.getAllDemandesWithDetails(pageable);

        return ResponseEntity.ok(demandes);
    }

    /**
     * Récupérer les demandes en attente - VERSION PAGINÉE
     */
    @GetMapping("/demandes/en-attente")
    public ResponseEntity<Page<DemandeDetailDTO>> getDemandesEnAttente(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateCreation,desc") String[] sort
    ) {
        log.info("GET /api/scolarite/demandes/en-attente - page: {}, size: {}", page, size);

        Pageable pageable = createPageable(page, size, sort);
        Page<DemandeDetailDTO> demandes = scolariteService.getDemandesEnAttente(pageable);

        return ResponseEntity.ok(demandes);
    }

    /**
     * Récupérer les demandes validées - VERSION PAGINÉE
     */
    @GetMapping("/demandes/validees")
    public ResponseEntity<Page<DemandeDetailDTO>> getDemandesValidees(
            @RequestParam(required = false) String login,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateCreation,desc") String[] sort
    ) {
        log.info("GET /api/scolarite/demandes/validees - login: {}, page: {}, size: {}", login, page, size);

        Pageable pageable = createPageable(page, size, sort);
        Page<DemandeDetailDTO> demandes = scolariteService.getDemandesValidees(login, pageable);

        return ResponseEntity.ok(demandes);
    }

    /**
     * Récupérer les demandes rejetées - VERSION PAGINÉE
     */
    @GetMapping("/demandes/rejetees")
    public ResponseEntity<Page<DemandeDetailDTO>> getDemandesRejetees(
            @RequestParam(required = false) String login,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateCreation,desc") String[] sort
    ) {
        log.info("GET /api/scolarite/demandes/rejetees - login: {}, page: {}, size: {}", login, page, size);

        Pageable pageable = createPageable(page, size, sort);
        Page<DemandeDetailDTO> demandes = scolariteService.getDemandesRejetees(login, pageable);

        return ResponseEntity.ok(demandes);
    }

    /**
     * ✅ NOUVEAU : Récupérer les dossiers en attente de document - VERSION PAGINÉE
     */
    @GetMapping("/demandes/en-attente-document")
    public ResponseEntity<Page<DemandeDetailDTO>> getDemandesEnAttenteDocument(
            @RequestParam(required = false) String login,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateCreation,desc") String[] sort
    ) {
        log.info("GET /api/scolarite/demandes/en-attente-document - login: {}, page: {}, size: {}", login, page, size);
        Pageable pageable = createPageable(page, size, sort);
        Page<DemandeDetailDTO> demandes = scolariteService.getDemandesEnAttenteDocument(login, pageable);
        return ResponseEntity.ok(demandes);
    }

    /**
     * Récupérer le détail d'une demande spécifique (pas de pagination)
     */
    @GetMapping("/demandes/{id}")
    public ResponseEntity<DemandeDetailDTO> getDemandeDetail(@PathVariable Long id) {
        log.info("GET /api/scolarite/demandes/{}", id);
        DemandeDetailDTO demande = scolariteService.getDemandeDetail(id);
        return ResponseEntity.ok(demande);
    }

    /**
     * Filtrer les demandes par diplôme - VERSION PAGINÉE
     */
    @GetMapping("/demandes/diplome/{nomDiplome}")
    public ResponseEntity<Page<DemandeDetailDTO>> getDemandesByDiplome(
            @PathVariable String nomDiplome,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateCreation,desc") String[] sort
    ) {
        log.info("GET /api/scolarite/demandes/diplome/{} - page: {}, size: {}", nomDiplome, page, size);

        Pageable pageable = createPageable(page, size, sort);
        Page<DemandeDetailDTO> demandes = scolariteService.getDemandesByDiplome(nomDiplome, pageable);

        return ResponseEntity.ok(demandes);
    }

    /**
     * Valider un dossier d'inscription
     */
    @PostMapping("/demandes/{id}/valider")
    public ResponseEntity<?> validerDossier(
            @PathVariable Long id,
            @Valid @RequestBody ValidationDossierRequest request
    ) {
        log.info("POST /api/scolarite/demandes/{}/valider - decision: {}", id, request.getDecision());

        try {
            // Mettre à jour le statut avec la décision
            StatusUpdateRequest statusUpdate = StatusUpdateRequest.builder()
                    .status(request.getDecision().equals("ACCEPTE") ?
                            "SCOLARITE_VALIDEE" : "REJETE_SCOLARITE")
                    .commentaire(request.getCommentaire())
                    .loginUtilisateur(request.getLoginUtilisateur())
                    .build();

            demandeService.updateStatus(id, statusUpdate);

            Map<String, Object> response = new HashMap<>();
            response.put("message", request.getDecision().equals("ACCEPTE") ?
                    "Dossier validé avec succès" : "Dossier rejeté");
            response.put("demandeId", id);
            response.put("decision", request.getDecision());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur lors de la validation du dossier {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Erreur lors de la validation: " + e.getMessage()));
        }
    }

    /**
     * Récupérer les statistiques pour le dashboard (pas de pagination)
     */
    @GetMapping("/statistiques")
    public ResponseEntity<Map<String, Object>> getStatistiques(
            @RequestParam(required = false) String login
    ) {
        log.info("GET /api/scolarite/statistiques");

        Pageable unpaged = Pageable.unpaged();

        Page<DemandeDetailDTO> nouveaux = scolariteService.getDemandesEnAttente(unpaged);
        Page<DemandeDetailDTO> enAttente = scolariteService.getDemandesEnAttenteDocument(login, unpaged);
        Page<DemandeDetailDTO> relancees = scolariteService.getDemandesRelancees(login, unpaged);
        Page<DemandeDetailDTO> validees = scolariteService.getDemandesValidees(login, unpaged);
        Page<DemandeDetailDTO> rejetees = scolariteService.getDemandesRejetees(login, unpaged);
        Page<DemandeDetailDTO> toutesLesDemandes = scolariteService.getAllDemandesWithDetails(unpaged);

        // Calculer le délai moyen de traitement
        long delaiMoyen = (long) validees.getContent().stream()
                .mapToDouble(DemandeDetailDTO::getEnAttenteDepuis)
                .average()
                .orElse(0.0);

        // Compter les urgents (priorité HAUTE) parmi les nouveaux
        long urgents = nouveaux.getContent().stream()
                .filter(d -> "HAUTE".equals(d.getPriorite()))
                .count();

        // Compter les dossiers incomplets parmi les nouveaux
        long dossiersIncomplets = nouveaux.getContent().stream()
                .filter(d -> d.getDocuments().stream()
                        .anyMatch(doc -> "MANQUANTE".equals(doc.getStatut()) || Boolean.FALSE.equals(doc.getIsValidated())))
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", toutesLesDemandes.getTotalElements());
        stats.put("enAttente", enAttente.getTotalElements()); 
        stats.put("nouveaux", nouveaux.getTotalElements()); 
        stats.put("relances", relancees.getTotalElements());
        stats.put("urgents", urgents);
        stats.put("validees", validees.getTotalElements());
        stats.put("rejetees", rejetees.getTotalElements());
        stats.put("dossiersIncomplets", dossiersIncomplets);
        stats.put("delaiMoyenTraitement", delaiMoyen + "h");

        return ResponseEntity.ok(stats);
    }

    /**
     * Méthode utilitaire pour créer un Pageable avec tri
     */
    private Pageable createPageable(int page, int size, String[] sort) {
        if (size > 100) {
            size = 100;
        }

        if (sort.length == 2) {
            String sortField = sort[0];
            String sortDirection = sort[1];

            Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ?
                    Sort.Direction.ASC : Sort.Direction.DESC;

            return PageRequest.of(page, size, Sort.by(direction, sortField));
        }

        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreation"));
    }

    /**
     * ✅ NOUVEAU : Récupérer les dossiers urgents - VERSION PAGINÉE
     */
    @GetMapping("/demandes/urgents")
    public ResponseEntity<Page<DemandeDetailDTO>> getDemandesUrgentes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateCreation,desc") String[] sort
    ) {
        log.info("GET /api/scolarite/demandes/urgents - page: {}, size: {}", page, size);
        Pageable pageable = createPageable(page, size, sort);
        Page<DemandeDetailDTO> demandes = scolariteService.getDemandesUrgentes(pageable);
        return ResponseEntity.ok(demandes);
    }
    @GetMapping("/demandes/nouveaux")
    public ResponseEntity<Page<DemandeDetailDTO>> getDemandesNouvelles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateCreation,desc") String[] sort
    ) {
        Pageable pageable = createPageable(page, size, sort);
        return ResponseEntity.ok(scolariteService.getDemandesNouvelles(pageable));
    }

    @GetMapping("/demandes/relancees")
    public ResponseEntity<Page<DemandeDetailDTO>> getDemandesRelancees(
            @RequestParam(required = false) String login,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateCreation,desc") String[] sort
    ) {
        Pageable pageable = createPageable(page, size, sort);
        return ResponseEntity.ok(scolariteService.getDemandesRelancees(login, pageable));
    }
}