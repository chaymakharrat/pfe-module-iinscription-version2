package pfe.example.enrollement_module.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pfe.example.enrollement_module.client.EtudiantServiceClient;
import pfe.example.enrollement_module.dto.DemandeInscriptionDTO;
import pfe.example.enrollement_module.dto.EtudiantInfoDTO;
import pfe.example.enrollement_module.dto.dashboard.DashboardStatsDTO;
import pfe.example.enrollement_module.dto.dashboard.WorkflowDistributionDTO;
import pfe.example.enrollement_module.entities.DemandeInscription;
import pfe.example.enrollement_module.entities.HistoriqueStatus;
import pfe.example.enrollement_module.enumerateur.StatutDemandeInscription;
import pfe.example.enrollement_module.repository.DemandeInscriptionRepository;
import pfe.example.enrollement_module.repository.HistoriqueStatusRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardAdminService {

    private final DemandeInscriptionRepository demandeInscriptionRepository;
    private final HistoriqueStatusRepository historiqueRepository;
    private final EtudiantServiceClient studentClient;

    // ========== MÉTHODES PAGINÉES (NOUVELLES) ==========

    /**
     * ✅ Récupère toutes les demandes avec pagination
     */
    public Page<DemandeInscriptionDTO> getAllDemandesPageable(Pageable pageable) {
        log.info("📄 Dashboard Admin - Récupération de toutes les demandes paginées - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<DemandeInscription> demandesPage = demandeInscriptionRepository.findAll(pageable);

        List<DemandeInscriptionDTO> demandesDTO = demandesPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(demandesDTO, pageable, demandesPage.getTotalElements());
    }

    /**
     * ✅ Récupère les demandes en attente avec pagination
     * (SOUMIS, EN_COURS_SCOLARITE, EN_COURS_DEPARTEMENT)
     */
    public Page<DemandeInscriptionDTO> getDemandesEnAttentePageable(Pageable pageable) {
        log.info("⏳ Dashboard Admin - Récupération des demandes en attente paginées");

        List<DemandeInscription> toutesLesDemandes = demandeInscriptionRepository.findAll();

        List<DemandeInscription> demandesEnAttente = toutesLesDemandes.stream()
                .filter(d -> {
                    StatutDemandeInscription statut = getDernierStatut(d.getId());
                    return statut == StatutDemandeInscription.SOUMIS ||
                            statut == StatutDemandeInscription.EN_COURS_SCOLARITE ||
                            statut == StatutDemandeInscription.EN_COURS_DEPARTEMENT;
                })
                .collect(Collectors.toList());

        return paginateList(demandesEnAttente, pageable);
    }

    /**
     * ✅ Récupère les demandes avec paiement validé avec pagination
     */
    public Page<DemandeInscriptionDTO> getDemandesPaymentValidPageable(Pageable pageable) {
        log.info("💳 Dashboard Admin - Récupération des paiements validés paginés");

        List<DemandeInscription> toutesLesDemandes = demandeInscriptionRepository.findAll();

        List<DemandeInscription> paiementsValides = toutesLesDemandes.stream()
                .filter(d -> getDernierStatut(d.getId()) == StatutDemandeInscription.PAIEMENT_VALIDE)
                .collect(Collectors.toList());

        return paginateList(paiementsValides, pageable);
    }

    /**
     * ✅ Récupère les demandes rejetées avec pagination
     */
    public Page<DemandeInscriptionDTO> getDemandesRejeteesPageable(Pageable pageable) {
        log.info("❌ Dashboard Admin - Récupération des demandes rejetées paginées");

        List<DemandeInscription> toutesLesDemandes = demandeInscriptionRepository.findAll();

        List<DemandeInscription> demandesRejetees = toutesLesDemandes.stream()
                .filter(d -> {
                    StatutDemandeInscription statut = getDernierStatut(d.getId());
                    return statut == StatutDemandeInscription.REJETE_SCOLARITE ||
                            statut == StatutDemandeInscription.REJETE_DEPARTEMENT ||
                            statut == StatutDemandeInscription.REJETE_FINANCE;
                })
                .collect(Collectors.toList());

        return paginateList(demandesRejetees, pageable);
    }

    /**
     * ✅ Récupère les inscrits définitifs avec pagination
     */
    public Page<DemandeInscriptionDTO> getInscritsDefinitivsPageable(Pageable pageable) {
        log.info("✅ Dashboard Admin - Récupération des inscrits définitifs paginés");

        List<DemandeInscription> toutesLesDemandes = demandeInscriptionRepository.findAll();

        List<DemandeInscription> inscrits = toutesLesDemandes.stream()
                .filter(d -> getDernierStatut(d.getId()) == StatutDemandeInscription.INSCRIT)
                .collect(Collectors.toList());

        return paginateList(inscrits, pageable);
    }

    /**
     * ✅ Récupère les demandes par statut exact avec pagination
     */
    public Page<DemandeInscriptionDTO> getDemandesByStatutPageable(String statutStr, Pageable pageable) {
        log.info("🔍 Dashboard Admin - Récupération des demandes par statut: {}", statutStr);

        try {
            StatutDemandeInscription statut = StatutDemandeInscription.valueOf(statutStr.toUpperCase());
            Page<DemandeInscription> demandesPage = demandeInscriptionRepository.findByStatutActuel(statut, pageable);

            List<DemandeInscriptionDTO> demandesDTO = demandesPage.getContent().stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());

            return new PageImpl<>(demandesDTO, pageable, demandesPage.getTotalElements());
        } catch (IllegalArgumentException e) {
            log.error("Statut invalide: {}", statutStr);
            return Page.empty(pageable);
        }
    }

    /**
     * ✅ Recherche par nom/email/diplôme avec pagination
     */
    public Page<DemandeInscriptionDTO> searchDemandesPageable(String searchTerm, Pageable pageable) {
        log.info("🔍 Dashboard Admin - Recherche paginée avec terme: {}", searchTerm);

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllDemandesPageable(pageable);
        }

        List<DemandeInscription> toutesLesDemandes = demandeInscriptionRepository.findAll();

        List<DemandeInscription> resultat = toutesLesDemandes.stream()
                .filter(d -> {
                    try {
                        EtudiantInfoDTO etudiant = studentClient.getEtudiantById(d.getEtudiantId());
                        String nomComplet = (etudiant.getNom() + " " + etudiant.getPrenom()).toLowerCase();
                        String email = etudiant.getEmail().toLowerCase();
                        String diplome = d.getNomDiplome().toLowerCase();
                        String lanque=d.getLangueDiplome().toLowerCase();
                        String search = searchTerm.toLowerCase();

                        return nomComplet.contains(search) ||
                                email.contains(search) ||
                                diplome.contains(search) ||lanque.contains(search)||
                                d.getId().toString().contains(search);
                    } catch (Exception e) {
                        log.warn("Erreur recherche étudiant {}: {}", d.getEtudiantId(), e.getMessage());
                        return false;
                    }
                })
                .collect(Collectors.toList());

        return paginateList(resultat, pageable);
    }

    // ========== MÉTHODES UTILITAIRES ==========

    /**
     * Pagine une liste manuellement (comme dans ScolariteService)
     */
    private Page<DemandeInscriptionDTO> paginateList(List<DemandeInscription> demandes, Pageable pageable) {
        int totalElements = demandes.size();

        // Gérer le cas unpaged (pour les statistiques)
        if (pageable.isUnpaged()) {
            List<DemandeInscriptionDTO> allDTO = demandes.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
            return new PageImpl<>(allDTO, pageable, totalElements);
        }

        // Pagination normale
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), totalElements);

        List<DemandeInscription> pageContent;
        if (start > totalElements) {
            pageContent = new ArrayList<>();
        } else {
            pageContent = demandes.subList(start, end);
        }

        List<DemandeInscriptionDTO> pageDTO = pageContent.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(pageDTO, pageable, totalElements);
    }

    /**
     * Récupère le dernier statut d'une demande depuis l'historique
     */
    private StatutDemandeInscription getDernierStatut(Long demandeId) {
        List<HistoriqueStatus> historiques = historiqueRepository
                .findByDemandeInscriptionIdOrderByDateStatusDesc(demandeId);

        if (historiques.isEmpty()) {
            return StatutDemandeInscription.SOUMIS;
        }

        return historiques.get(0).getStatut();
    }

    /**
     * Convertit une DemandeInscription en DTO avec infos étudiant
     */
    private DemandeInscriptionDTO toDTO(DemandeInscription d) {
        // Récupérer les infos de l'étudiant via Feign
        EtudiantInfoDTO etudiant = null;
        try {
            etudiant = studentClient.getEtudiantById(d.getEtudiantId());
        } catch (Exception e) {
            log.error("⚠️ Erreur récupération étudiant {}: {}", d.getEtudiantId(), e.getMessage());
        }

        // Récupérer le dernier statut depuis l'historique (plus fiable que statutActuel)
        StatutDemandeInscription dernierStatut = getDernierStatut(d.getId());

        return DemandeInscriptionDTO.builder()
                .id(d.getId())
                .dateCreation(d.getDateCreation())
                .studentId(d.getEtudiantId())
                .diplomeDemande(d.getNomDiplome())
                .langueDiplome(d.getLangueDiplome())
                .processInstanceId(d.getProcessInstanceId())
                .statut(dernierStatut)  // ✅ Statut depuis historique
                .student(etudiant)
                .build();
    }

    // ========== MÉTHODES EXISTANTES (NON PAGINÉES) ==========

    /**
     * ⚠️ DÉPRÉCIÉ : Utiliser getAllDemandesPageable() à la place
     * Conservé pour compatibilité avec ancien code
     */
    @Deprecated
    public List<DemandeInscription> getAllDemandes() {
        log.warn("⚠️ Méthode getAllDemandes() dépréciée - Utiliser getAllDemandesPageable()");
        return demandeInscriptionRepository.findAll();
    }

    /**
     * ⚠️ DÉPRÉCIÉ : Utiliser getAllDemandesPageable() à la place
     * Conservé pour compatibilité avec ancien code
     */
    @Deprecated
    public List<DemandeInscriptionDTO> getAllDemandesAsDTO() {
        log.warn("⚠️ Méthode getAllDemandesAsDTO() dépréciée - Utiliser getAllDemandesPageable()");
        return demandeInscriptionRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Statistiques globales (pas de pagination nécessaire)
     */
    public DashboardStatsDTO getDashboardStats() {
        log.info("📊 Dashboard Admin - Récupération des statistiques");

        long total = demandeInscriptionRepository.count();

        // En attente = SOUMIS + EN_COURS_SCOLARITE + EN_COURS_DEPARTEMENT+EN_ATTENTE_PAIEMENT
        long enAttente = demandeInscriptionRepository.countByStatutActuel(StatutDemandeInscription.SOUMIS)
                + demandeInscriptionRepository.countByStatutActuel(StatutDemandeInscription.EN_COURS_SCOLARITE)
                + demandeInscriptionRepository.countByStatutActuel(StatutDemandeInscription.EN_COURS_DEPARTEMENT)
                + demandeInscriptionRepository.countByStatutActuel(StatutDemandeInscription.EN_ATTENTE_PAIEMENT);


        // Rejetés = tous les types de rejet
        long rejetes = demandeInscriptionRepository.countByStatutActuel(StatutDemandeInscription.REJETE_SCOLARITE)
                + demandeInscriptionRepository.countByStatutActuel(StatutDemandeInscription.REJETE_DEPARTEMENT)
                + demandeInscriptionRepository.countByStatutActuel(StatutDemandeInscription.REJETE_FINANCE);

        long paiementsValides = demandeInscriptionRepository.countByStatutActuel(StatutDemandeInscription.PAIEMENT_VALIDE);
        long inscrits = demandeInscriptionRepository.countByStatutActuel(StatutDemandeInscription.INSCRIT);

        return DashboardStatsDTO.builder()
                .totalInscriptions(total)
                .demandesEnAttente(enAttente)
                .dossiersRejetes(rejetes)
                .paiementsValides(paiementsValides)
                .inscritsDefinitifs(inscrits)
                .build();
    }

    /**
     * Distribution par statut pour le graphique (pas de pagination)
     */
    public List<WorkflowDistributionDTO> getWorkflowDistribution() {
        log.info("📈 Dashboard Admin - Récupération de la distribution des statuts");

        List<Object[]> results = demandeInscriptionRepository.countByStatus();
        return results.stream()
                .map(r -> new WorkflowDistributionDTO(r[0].toString(), (Long) r[1]))
                .collect(Collectors.toList());
    }
}