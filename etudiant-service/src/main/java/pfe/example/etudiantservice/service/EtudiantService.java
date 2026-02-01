package pfe.example.etudiantservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pfe.example.etudiantservice.client.DemandeInscriptionRestClient;
import pfe.example.etudiantservice.entities.Etudiant;
import pfe.example.etudiantservice.model.DemandeInscription;
import pfe.example.etudiantservice.repositories.EtudiantRepository;


import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;
    private final DemandeInscriptionRestClient clientDemandeInscription;

    public Etudiant inscrireEtudiant(Long candidatId) {

        // 1️⃣ Vérifier si déjà inscrit
        if (etudiantRepository.existsByCandidatId(candidatId)) {
            throw new RuntimeException("Cet étudiant est déjà inscrit");
        }

        // 2️⃣ Récupérer la demande via Feign
        DemandeInscription demande =
                clientDemandeInscription.getDemandeById(candidatId);

        // 3️⃣ Créer l'étudiant
        Etudiant etudiant = new Etudiant();
        etudiant.setCandidatId(demande.getId());
        etudiant.setDateInscription(LocalDateTime.now());
        etudiant.setMatricule(generateMatricule());

        Etudiant saved = etudiantRepository.save(etudiant);

        // 4️⃣ Mettre à jour le statut côté enrollment
        clientDemandeInscription.accepterDemande(candidatId);

        return saved;
    }

    private String generateMatricule() {
        return "UNI-" + UUID.randomUUID().toString().substring(0, 8);
    }
    // Récupérer un étudiant par son ID
    public Etudiant getEtudiantById(Long id) {
        return etudiantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé avec id: " + id));
    }

    // Récupérer un étudiant par le candidatId
    public Etudiant getEtudiantByCandidatId(Long candidatId) {
        return etudiantRepository.findByCandidatId(candidatId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé pour le candidatId: " + candidatId));
    }

}
