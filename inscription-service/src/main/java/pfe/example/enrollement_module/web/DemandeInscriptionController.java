package pfe.example.enrollement_module.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pfe.example.enrollement_module.client.EtudiantServiceClient;
import pfe.example.enrollement_module.dto.DemandeInscriptionDTO;
import pfe.example.enrollement_module.dto.EtudiantInfoDTO;
import pfe.example.enrollement_module.dto.HistoriqueStatut.HistoriqueRequest;
import pfe.example.enrollement_module.dto.HistoriqueStatut.StatusUpdateRequest;
import pfe.example.enrollement_module.entities.DemandeInscription;
import pfe.example.enrollement_module.services.DemandeInscriptionService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/demandes")
@RequiredArgsConstructor
public class DemandeInscriptionController {

    private final DemandeInscriptionService demandeInscriptionService;
    private final EtudiantServiceClient etudiantServiceClient;

    @PostMapping
    public ResponseEntity<DemandeInscription> submitCandidature(
            @RequestBody DemandeInscription request
    ) {
        return ResponseEntity.ok(
                demandeInscriptionService.submitCandidature(request)
        );
    }

    // 🆕 Pour Workflow: ajout historique
    @PostMapping("/enrollments/{id}/historique")
    public ResponseEntity<Void> addHistorique(
            @PathVariable Long id,
            @RequestBody HistoriqueRequest request
    ) {
        demandeInscriptionService.addHistoriqueFromWorkflow(id, request);
        return ResponseEntity.ok().build();
    }
    // 🆕 Mettre à jour le statut d'une demande
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request
    ) {
        demandeInscriptionService.updateStatus(id, request);
        return ResponseEntity.ok().build();
    }
    // 🆕 Récupérer UNE demande par ID (utilisé par WORKFLOW-SERVICE)
    @GetMapping("{id}")
    public ResponseEntity<DemandeInscriptionDTO> getEnrollmentById(@PathVariable Long id) {
        DemandeInscription demande = demandeInscriptionService.getEnrollment(id);

        // Convertir l'entité en DTO
        DemandeInscriptionDTO dto = DemandeInscriptionDTO.builder()
                .id(demande.getId())
                .dateCreation(demande.getDateCreation())
                .studentId(demande.getEtudiantId())
                .diplomeDemande(demande.getNomDiplome())
                .processInstanceId(demande.getProcessInstanceId())
                .statut(demande.getStatutActuel())
                .tokenAcces(demande.getTokenAcces())
                .build();

        return ResponseEntity.ok(dto);
    }

    // 🆕 Générer un token à la demande
    @PostMapping("/{id}/token")
    public ResponseEntity<String> generateToken(@PathVariable Long id) {
        return ResponseEntity.ok(demandeInscriptionService.generateToken(id));
    }

    // 🆕 Signal resubmission of documents
    @PostMapping("/{id}/resubmit")
    public ResponseEntity<Void> resubmitDocuments(@PathVariable Long id) {
        demandeInscriptionService.resubmitDocuments(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/etudiant/{etudiantId}")
    public ResponseEntity<DemandeInscriptionDTO> getEnrollmentByEtudiantId(@PathVariable Long etudiantId) {
        DemandeInscription demande = demandeInscriptionService.getDemandeByEtudiantId(etudiantId);
        DemandeInscriptionDTO dto = DemandeInscriptionDTO.builder()
                .id(demande.getId())
                .dateCreation(demande.getDateCreation())
                .studentId(demande.getEtudiantId())
                .diplomeDemande(demande.getNomDiplome())
                .processInstanceId(demande.getProcessInstanceId())
                .statut(demande.getStatutActuel())
                .tokenAcces(demande.getTokenAcces())
                .build();
        return ResponseEntity.ok(dto);
    }

    // 🆕 ACCÈS PUBLIC via TOKEN (sans authentification)
    @GetMapping("/public/token/{token}")
    public ResponseEntity<DemandeInscriptionDTO> getDemandeByToken(@PathVariable String token) {
        DemandeInscription demande = demandeInscriptionService.getDemandeByToken(token);
        DemandeInscriptionDTO dto = DemandeInscriptionDTO.builder()
                .id(demande.getId())
                .dateCreation(demande.getDateCreation())
                .studentId(demande.getEtudiantId())
                .diplomeDemande(demande.getNomDiplome())
                .processInstanceId(demande.getProcessInstanceId())
                .statut(demande.getStatutActuel())
                .tokenAcces(demande.getTokenAcces())
                .build();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/public/token/{token}/resubmit")
    public ResponseEntity<Void> resubmitByToken(@PathVariable String token) {
        demandeInscriptionService.resubmitByToken(token);
        return ResponseEntity.ok().build();
    }
    // Dans DemandeInscriptionController.java — ajouter :
    @GetMapping("/diplome/{nomDiplome}/langue/{langue}")
    public ResponseEntity<List<DemandeInscriptionDTO>> getDemandesByDiplome(
            @PathVariable String nomDiplome,
            @PathVariable String langue) {

        List<DemandeInscription> demandes =
                demandeInscriptionService.getDemandesByDiplome(nomDiplome, langue);

        List<DemandeInscriptionDTO> dtos = demandes.stream()
                .map(demande -> {
                    // ✅ Récupérer les infos étudiant via Feign
                    EtudiantInfoDTO studentInfo = null;
                    try {
                        studentInfo = etudiantServiceClient.getEtudiantById(demande.getEtudiantId());
                    } catch (Exception e) {
                        System.out.println("⚠️ Impossible de récupérer étudiant id={}: {}"+
                                demande.getEtudiantId()+ e.getMessage());
                    }

                    return DemandeInscriptionDTO.builder()
                            .id(demande.getId())
                            .dateCreation(demande.getDateCreation())
                            .studentId(demande.getEtudiantId())
                            .diplomeDemande(demande.getNomDiplome())
                            .langueDiplome(demande.getLangueDiplome())
                            .processInstanceId(demande.getProcessInstanceId())
                            .statut(demande.getStatutActuel())
                            .tokenAcces(demande.getTokenAcces())
                            .student(studentInfo) // ✅ peuplé
                            .build();
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
    @GetMapping("/diplome/{nomDiplome}")
    public ResponseEntity<List<DemandeInscriptionDTO>> getDemandesByDiplomeAllLangues(
            @PathVariable String nomDiplome) {

        List<DemandeInscription> demandes =
                demandeInscriptionService.getDemandesByDiplomeAllLangues(nomDiplome);

        List<DemandeInscriptionDTO> dtos = demandes.stream()
                .map(demande -> {
                    EtudiantInfoDTO studentInfo = null;
                    try {
                        studentInfo = etudiantServiceClient
                                .getEtudiantById(demande.getEtudiantId());
                    } catch (Exception e) {
                        System.out.println("⚠️ Étudiant introuvable id={}"+ demande.getEtudiantId());
                    }
                    return DemandeInscriptionDTO.builder()
                            .id(demande.getId())
                            .dateCreation(demande.getDateCreation())
                            .studentId(demande.getEtudiantId())
                            .diplomeDemande(demande.getNomDiplome())
                            .langueDiplome(demande.getLangueDiplome())
                            .processInstanceId(demande.getProcessInstanceId())
                            .statut(demande.getStatutActuel())
                            .tokenAcces(demande.getTokenAcces())
                            .student(studentInfo)
                            .build();
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}