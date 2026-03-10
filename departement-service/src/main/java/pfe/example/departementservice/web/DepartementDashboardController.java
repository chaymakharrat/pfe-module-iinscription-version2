package pfe.example.departementservice.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pfe.example.departementservice.dto.CapaciteNiveauDTO;
import pfe.example.departementservice.dto.DashboardDeptDTO;
import pfe.example.departementservice.dto.DemandeDeptDTO;
import pfe.example.departementservice.dto.StatsRapideDTO;
import pfe.example.departementservice.service.DepartementDashboardService;

import java.awt.print.Pageable;
import java.util.List;


@RestController
@RequestMapping("/api/dashboardDepartment")
@RequiredArgsConstructor
@Slf4j
public class DepartementDashboardController {

    private final DepartementDashboardService service;

    /**
     * Dashboard complet — premier chargement de la page
     * GET /api/dashboardDepartment/dashboard?email=...
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDeptDTO> getDashboard(@RequestParam String email) {
        log.info("📊 Dashboard complet — email: {}", email);
        return ResponseEntity.ok(service.getDashboard(clean(email)));
    }

    /**
     * Stats légères — polling toutes les 30s pour mettre à jour les compteurs
     * GET /api/dashboardDepartment/stats?email=...
     */
    @GetMapping("/stats")
    public ResponseEntity<StatsRapideDTO> getStats(@RequestParam String email) {
        log.info("📈 Stats rapides — email: {}", email);
        return ResponseEntity.ok(service.getStatsRapides(clean(email)));
    }

    /**
     * Capacités par niveau — peut être rafraîchi indépendamment
     * GET /api/dashboardDepartment/capacites?email=...
     */
    @GetMapping("/capacites")
    public ResponseEntity<List<CapaciteNiveauDTO>> getCapacites(@RequestParam String email) {
        log.info("🏫 Capacités — email: {}", email);
        return ResponseEntity.ok(service.getCapacites(clean(email)));
    }

    /**
     * Demandes filtrées et paginées — alimentent le tableau principal
     * GET /api/dashboardDepartment/demandes?email=...&statut=EN_COURS_DEPARTEMENT&search=ali&page=0&size=20
     */
    @GetMapping("/demandes")
    public ResponseEntity<Page<DemandeDeptDTO>> getDemandes(
            @RequestParam String email,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("📋 Demandes — email: {}, statut: {}, search: {}", email, statut, search);
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.getDemandes(clean(email), statut, search, pageable));
    }

    /**
     * Détail d'une demande — ouvre le modal
     * GET /api/dashboardDepartment/demandes/42?email=...
     */
    @GetMapping("/demandes/{id}")
    public ResponseEntity<DemandeDeptDTO> getDetailDemande(
            @RequestParam String email,
            @PathVariable Long id) {

        log.info("🔍 Détail demande {} — email: {}", id, email);
        return ResponseEntity.ok(service.getDetailDemande(clean(email), id));
    }

    // Helper — nettoie l'email reçu
    private String clean(String email) {
        return email.trim().toLowerCase();
    }
}