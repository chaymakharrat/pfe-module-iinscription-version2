package pfe.example.departementservice.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pfe.example.departementservice.client.EnrollmentServiceClient;
import pfe.example.departementservice.dto.*;
import pfe.example.departementservice.entities.*;
import pfe.example.departementservice.repository.EnseignantRepository;
import pfe.example.departementservice.repository.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DepartementDashboardService {

    private final EnseignantRepository enseignantRepository;
    private final NiveauDiplomeSpecifiqueRepository niveauSpecifiqueRepo;
    private final EnrollmentServiceClient enrollmentClient;

    public DashboardDeptDTO getDashboard(String emailEnseignant) {

        // 1. Trouver enseignant et son diplôme
        Enseignant enseignant = enseignantRepository
                .findByEmailUniversitaire(emailEnseignant)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Enseignant non trouvé: " + emailEnseignant));

        DiplomeEtudier diplome = enseignant.getDiplomeEtudier();

        // 2. Récupérer toutes les demandes EN_COURS_DEPARTEMENT
        List<DemandeInfoDTO> demandes = enrollmentClient
                .getDemandesByDiplome(diplome.getNom(), diplome.getLangue().name());

        // 3. Stats
        long enCours = demandes.stream()
                .filter(d -> "EN_COURS_DEPARTEMENT".equals(d.getStatut())).count();
        long valides = demandes.stream()
                .filter(d -> "DEPARTEMENT_VALIDE".equals(d.getStatut())
                        || "INSCRIT".equals(d.getStatut())).count();
        long rejetes = demandes.stream()
                .filter(d -> "REJETE_DEPARTEMENT".equals(d.getStatut())).count();
        long listeAttente = demandes.stream()
                .filter(d -> "LISTE_ATTENTE".equals(d.getStatut())).count();

        // 4. Capacités par niveau
        List<Niveau_diplome_specifique> niveaux =
                niveauSpecifiqueRepo.findByDiplome(diplome);

        List<CapaciteNiveauDTO> capacites = niveaux.stream()
                .map(n -> {
                    int confirmes = (int) demandes.stream()
                            .filter(d -> "INSCRIT".equals(d.getStatut())).count();
                    int enTraitement = (int) enCours;
                    int attente = (int) listeAttente;
                    int restantes = n.getCapaciteMax() - confirmes - enTraitement;
                    double pct = (double)(confirmes + enTraitement)
                            / n.getCapaciteMax() * 100;

                    return CapaciteNiveauDTO.builder()
                            .niveau(n.getNiveau().getNiveau())
                            .capaciteMax(n.getCapaciteMax())
                            .inscritsConfirmes(confirmes)
                            .enCoursTraitement(enTraitement)
                            .listeAttente(attente)
                            .placesRestantes(Math.max(0, restantes))
                            .pourcentageRemplissage(Math.min(100, pct))
                            .build();
                })
                .collect(Collectors.toList());

        // 5. Construire demandes avec prérequis
        List<DemandeDeptDTO> demandeDTOs = demandes.stream()
                .filter(d -> "EN_COURS_DEPARTEMENT".equals(d.getStatut()))
                .map(d -> buildDemandeDeptDTO(d, diplome))
                .collect(Collectors.toList());

        return DashboardDeptDTO.builder()
                .nomDepartement(diplome.getDepartement().getNom())
                .nomEnseignant(emailEnseignant)
                .emailEnseignant(emailEnseignant)
                .nomDiplome(diplome.getNom())
                .langue(diplome.getLangue().name())
                .enCours((int) enCours)
                .valides((int) valides)
                .rejetes((int) rejetes)
                .listeAttente((int) listeAttente)
                .capacites(capacites)
                .demandes(demandeDTOs)
                .build();
    }

    private DemandeDeptDTO buildDemandeDeptDTO(
            DemandeInfoDTO demande, DiplomeEtudier diplome) {

        // Vérifier prérequis via diplome.getType().getPrerequis()
        List<PrerequisDetailDTO> details = new ArrayList<>();
        boolean tousConformes = true;

        if (diplome.getType() != null) {
            for (Prerequis p : diplome.getType().getPrerequis()) {
                boolean conforme = verifierPrerequis(
                        p.getNom(), demande.getDiplomeObtenu());
                if (!conforme) tousConformes = false;
                details.add(PrerequisDetailDTO.builder()
                        .prerequisRequis(p.getNom())
                        .valeurCandidat(demande.getDiplomeObtenu())
                        .conforme(conforme)
                        .build());
            }
        }

        return DemandeDeptDTO.builder()
                .id(demande.getId())
                .etudiantId(demande.getStudentId())
                .nomDiplome(demande.getNomDiplome())
                .langue(demande.getLangueDiplome())
                .dateCreation(demande.getDateCreation())
                .statut(demande.getStatut())
                .prerequisSatisfaits(tousConformes)
                .prerequisDetails(details)
                .diplomeObtenu(demande.getDiplomeObtenu())
                .build();
    }

    private boolean verifierPrerequis(String prerequisNom, String diplomeObtenu) {
        if (diplomeObtenu == null) return false;
        return diplomeObtenu.toUpperCase()
                .contains(prerequisNom.toUpperCase());
    }
}
