package pfe.example.enrollement_module.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pfe.example.enrollement_module.client.EtudiantServiceClient;
import pfe.example.enrollement_module.dto.*;
import pfe.example.enrollement_module.dto.HistoriqueStatut.HistoriqueStatusDTO;
import pfe.example.enrollement_module.entities.DemandeInscription;
import pfe.example.enrollement_module.entities.HistoriqueStatus;
import pfe.example.enrollement_module.enumerateur.StatutDemandeInscription;
import pfe.example.enrollement_module.repository.DemandeInscriptionRepository;
import pfe.example.enrollement_module.repository.HistoriqueStatusRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScolariteService {

    private final DemandeInscriptionRepository demandeRepository;
    private final HistoriqueStatusRepository historiqueRepository;
    private final EtudiantServiceClient etudiantClient;

    private List<DemandeInscription> toList(Iterable<DemandeInscription> iterable) {
        List<DemandeInscription> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

    /**
     * Récupère toutes les demandes avec détails enrichis - VERSION PAGINÉE
     */
    public Page<DemandeDetailDTO> getAllDemandesWithDetails(Pageable pageable) {
        Page<DemandeInscription> demandesPage = demandeRepository.findAll(pageable);

        List<DemandeDetailDTO> demandesDTO = demandesPage.getContent().stream()
                .map(this::enrichDemandeWithDetails)
                .collect(Collectors.toList());

        return new PageImpl<>(demandesDTO, pageable, demandesPage.getTotalElements());
    }

    /**
     * Récupère les demandes en attente de validation scolarité - VERSION PAGINÉE
     * ✅ MODIFIÉ : Filtre uniquement EN_COURS_SCOLARITE et SOUMIS
     */
    public Page<DemandeDetailDTO> getDemandesEnAttente(Pageable pageable) {
        List<DemandeInscription> toutesLesDemandes = toList(demandeRepository.findAll());

        // ✅ Filtrer UNIQUEMENT les demandes en attente de validation scolarité
        List<DemandeInscription> demandesEnAttente = toutesLesDemandes.stream()
                .filter(d -> {
                    StatutDemandeInscription dernierStatut = getDernierStatut(d.getId());
                    // ✅ Seulement EN_COURS_SCOLARITE et SOUMIS
                    return dernierStatut == StatutDemandeInscription.EN_COURS_SCOLARITE ||
                            dernierStatut == StatutDemandeInscription.SOUMIS;
                })
                .collect(Collectors.toList());

        // Appliquer la pagination manuellement
        return paginateList(demandesEnAttente, pageable);
    }
    // Nouveaux : < 24h
    public Page<DemandeDetailDTO> getDemandesNouvelles(Pageable pageable) {
        List<DemandeInscription> toutes = toList(demandeRepository.findAll());
        List<DemandeInscription> filtered = toutes.stream()
                .filter(d -> {
                    StatutDemandeInscription statut = getDernierStatut(d.getId());
                    if (statut != StatutDemandeInscription.SOUMIS &&
                            statut != StatutDemandeInscription.EN_COURS_SCOLARITE) return false;
                    long heures = Duration.between(d.getDateCreation(), LocalDateTime.now()).toHours();
                    return heures < 24;
                })
                .collect(Collectors.toList());
        return paginateList(filtered, pageable);
    }

    // Urgents : > 24h (au lieu de > 96h)
    public Page<DemandeDetailDTO> getDemandesUrgentes(Pageable pageable) {
        List<DemandeInscription> toutes = toList(demandeRepository.findAll());
        List<DemandeInscription> filtered = toutes.stream()
                .filter(d -> {
                    StatutDemandeInscription statut = getDernierStatut(d.getId());
                    if (statut != StatutDemandeInscription.SOUMIS &&
                            statut != StatutDemandeInscription.EN_COURS_SCOLARITE) return false;
                    long heures = Duration.between(d.getDateCreation(), LocalDateTime.now()).toHours();
                    return heures >= 24;
                })
                .collect(Collectors.toList());
        return paginateList(filtered, pageable);
    }

    /**
     * ✅ NOUVEAU : Récupère les dossiers complets (tous les documents sont SOUMIS)
     */
    public Page<DemandeDetailDTO> getDemandesCompletes(Pageable pageable) {
        List<DemandeInscription> toutesLesDemandes = toList(demandeRepository.findAll());

        List<DemandeInscription> demandesCompletes = toutesLesDemandes.stream()
                .filter(d -> {
                    StatutDemandeInscription dernierStatut = getDernierStatut(d.getId());

                    if (dernierStatut != StatutDemandeInscription.SOUMIS &&
                        dernierStatut != StatutDemandeInscription.EN_COURS_SCOLARITE) {
                        return false;
                    }

                    try {
                        List<DocumentStatusDTO> documents = etudiantClient.getDocumentsStatus(d.getEtudiantId());
                        if (documents.isEmpty()) return false;
                        // ✅ Tous doivent être SOUMIS et VALIDÉS
                        return documents.stream().allMatch(doc ->
                                "SOUMIS".equals(doc.getStatut()) && Boolean.TRUE.equals(doc.getIsValidated()));
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());

        return paginateList(demandesCompletes, pageable);
    }

    /**
     * ✅ MODIFIÉ : Récupère les demandes validées (SCOLARITE_VALIDEE uniquement)
     * N'affiche PAS les dossiers EN_COURS_DEPARTEMENT
     */
    public Page<DemandeDetailDTO> getDemandesValidees(String login, Pageable pageable) {
        List<DemandeInscription> toutes = toList(demandeRepository.findAll());
        List<DemandeInscription> filtered = toutes.stream()
                .filter(d -> {
                    // ✅ Chercher dans TOUT l'historique, pas seulement le dernier
                    List<HistoriqueStatus> historiques = historiqueRepository
                            .findByDemandeInscriptionIdOrderByDateStatusDesc(d.getId());
                    return historiques.stream()
                            .anyMatch(h -> h.getStatut() == StatutDemandeInscription.SCOLARITE_VALIDEE);
                })
                .collect(Collectors.toList());
        return paginateList(filtered, pageable);
    }

    /**
     * Récupère les demandes rejetées - VERSION PAGINÉE
     */
    public Page<DemandeDetailDTO> getDemandesRejetees(String login, Pageable pageable) {
        List<DemandeInscription> toutesLesDemandes = toList(demandeRepository.findAll());

        List<DemandeInscription> demandesRejetees = toutesLesDemandes.stream()
                .filter(d -> {
                    HistoriqueStatus h = getDernierHistorique(d.getId());
                    if (h == null) return false;
                    boolean isRejete = h.getStatut() == StatutDemandeInscription.REJETE_SCOLARITE;
                    if (login == null || login.isEmpty()) return isRejete;
                    return isRejete && login.equals(h.getLoginUtilisateur());
                })
                .collect(Collectors.toList());

        return paginateList(demandesRejetees, pageable);
    }

    /**
     * ✅ NOUVEAU : Récupère les demandes en attente de documents traitées par l'utilisateur
     */
    public Page<DemandeDetailDTO> getDemandesEnAttenteDocument(String login, Pageable pageable) {
        List<DemandeInscription> toutes = toList(demandeRepository.findAll());
        List<DemandeInscription> filtered = toutes.stream()
                .filter(d -> {
                    HistoriqueStatus h = getDernierHistorique(d.getId());
                    if (h == null) return false;
                    boolean isAttente = h.getStatut() == StatutDemandeInscription.EN_ATTENTE_DOCUMENT;
                    if (login == null || login.isEmpty()) return isAttente;
                    return isAttente && login.equals(h.getLoginUtilisateur());
                })
                .collect(Collectors.toList());
        return paginateList(filtered, pageable);
    }

    /**
     * ✅ Récupère les demandes relancées (statut RELANCE) pour l'agent qui a envoyé la demande EN_ATTENTE_DOCUMENT.
     * Si login est vide, retourne toutes les relancées.
     */
    public Page<DemandeDetailDTO> getDemandesRelancees(String login, Pageable pageable) {
        List<DemandeInscription> toutes = toList(demandeRepository.findAll());
        List<DemandeInscription> filtered = toutes.stream()
                .filter(d -> {
                    if (getDernierStatut(d.getId()) != StatutDemandeInscription.RELANCE) return false;
                    if (login == null || login.isEmpty()) return true;
                    // On cherche le dernier historique EN_ATTENTE_DOCUMENT pour savoir quel agent a fait la relance
                    List<HistoriqueStatus> historiques = historiqueRepository
                            .findByDemandeInscriptionIdOrderByDateStatusDesc(d.getId());
                    return historiques.stream()
                            .filter(h -> h.getStatut() == StatutDemandeInscription.EN_ATTENTE_DOCUMENT)
                            .findFirst()
                            .map(h -> login.equals(h.getLoginUtilisateur()))
                            .orElse(false);
                })
                .collect(Collectors.toList());
        return paginateList(filtered, pageable);
    }

    /**
     * Filtre les demandes par diplôme - VERSION PAGINÉE
     */
    public Page<DemandeDetailDTO> getDemandesByDiplome(String nomDiplome, Pageable pageable) {
        List<DemandeInscription> toutesLesDemandes = toList(demandeRepository.findAll());

        List<DemandeInscription> demandesFiltrees = toutesLesDemandes.stream()
                .filter(d -> d.getNomDiplome().toLowerCase().contains(nomDiplome.toLowerCase()))
                .collect(Collectors.toList());

        return paginateList(demandesFiltrees, pageable);
    }

    /**
     * Récupère le détail d'une demande spécifique (pas de pagination)
     */
    public DemandeDetailDTO getDemandeDetail(Long demandeId) {
        DemandeInscription demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        return enrichDemandeWithDetails(demande);
    }

    /**
     * Méthode utilitaire pour paginer une liste manuellement
     */
    private Page<DemandeDetailDTO> paginateList(List<DemandeInscription> demandes, Pageable pageable) {
        int totalElements = demandes.size();

        // Gérer le cas unpaged
        if (pageable.isUnpaged()) {
            List<DemandeDetailDTO> allDTO = demandes.stream()
                    .map(this::enrichDemandeWithDetails)
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

        List<DemandeDetailDTO> pageDTO = pageContent.stream()
                .map(this::enrichDemandeWithDetails)
                .collect(Collectors.toList());

        return new PageImpl<>(pageDTO, pageable, totalElements);
    }

    /**
     * Enrichit une demande avec les détails de l'étudiant et des documents
     */
    private DemandeDetailDTO enrichDemandeWithDetails(DemandeInscription demande) {
        try {
            // Récupérer les infos de l'étudiant
            EtudiantInfoDTO etudiant = etudiantClient.getEtudiantById(demande.getEtudiantId());

            // Récupérer les documents
            List<DocumentStatusDTO> documents = etudiantClient.getDocumentsStatus(demande.getEtudiantId());

            // Récupérer l'historique
            List<HistoriqueStatus> historiques = historiqueRepository
                    .findByDemandeInscriptionIdOrderByDateStatusDesc(demande.getId());

            List<HistoriqueStatusDTO> historiqueDTOs = historiques.stream()
                    .map(h -> HistoriqueStatusDTO.builder()
                            .id(h.getId())
                            .statut(h.getStatut().name())
                            .commentaire(h.getCommentaire())
                            .loginUtilisateur(h.getLoginUtilisateur())
                            .dateStatus(h.getDateStatus())
                            .build())
                    .collect(Collectors.toList());

            // Calculer le temps d'attente
            LocalDateTime referenceDate = demande.getDateCreation();
            StatutDemandeInscription statutActuel = getDernierStatut(demande.getId());

            // ✅ Si en attente de document, on calcule depuis le changement de statut
            if (statutActuel == StatutDemandeInscription.EN_ATTENTE_DOCUMENT && !historiques.isEmpty()) {
                referenceDate = historiques.get(0).getDateStatus();
            }

            Duration duration = Duration.between(referenceDate, LocalDateTime.now());
            double enAttenteDepuis = duration.toMillis() / (1000.0 * 60.0 * 60.0);

            // Déterminer la priorité
            String priorite = calculerPriorite((long)enAttenteDepuis, documents);

            // Déterminer le statut actuel (déjà récupéré plus haut)
            return DemandeDetailDTO.builder()
                    .id(demande.getId())
                    .numeroDossier("#ETU-" + demande.getDateCreation().getYear() + "-" + String.format("%04d", demande.getId()))
                    .etudiantId(demande.getEtudiantId())
                    .nomDiplome(demande.getNomDiplome())
                    .langueDiplome(demande.getLangueDiplome())
                    .statutActuel(statutActuel)
                    .dateCreation(demande.getDateCreation())
                    .processInstanceId(demande.getProcessInstanceId())
                    .etudiant(etudiant)
                    .documents(documents)
                    .historique(historiqueDTOs)
                    .enAttenteDepuis(enAttenteDepuis)
                    .priorite(priorite)
                    .build();

        } catch (Exception e) {
            log.error("Erreur lors de l'enrichissement de la demande {}: {}", demande.getId(), e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération des détails de la demande", e);
        }
    }

    /**
     * Calcule la priorité en fonction du temps d'attente et des documents
     */
    private String calculerPriorite(Long heuresAttente, List<DocumentStatusDTO> documents) {
        long documentsValides = documents.stream()
                .filter(d -> "SOUMIS".equals(d.getStatut()))
                .count();

        boolean tousDocuments = !documents.isEmpty() && documents.stream()
                .allMatch(d -> "SOUMIS".equals(d.getStatut()));

        // ✅ Seuil à 4 jours (96 heures) pour Urgent
        if (heuresAttente > 96) {
            return "HAUTE";
        } else if (heuresAttente > 48 || tousDocuments) {
            return "MOYENNE";
        } else {
            return "BASSE";
        }
    }

    /**
     * Récupère le dernier statut d'une demande
     */
    private StatutDemandeInscription getDernierStatut(Long demandeId) {
        //normalment ba3d nzid el login njib dernier historique eli 3melha heka el agent
        HistoriqueStatus h = getDernierHistorique(demandeId);
        return (h != null) ? h.getStatut() : StatutDemandeInscription.SOUMIS;
    }

    private HistoriqueStatus getDernierHistorique(Long demandeId) {
        List<HistoriqueStatus> historiques = historiqueRepository
                .findByDemandeInscriptionIdOrderByDateStatusDesc(demandeId);
        return historiques.isEmpty() ? null : historiques.get(0);
    }

}