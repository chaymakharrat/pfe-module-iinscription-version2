package pfe.example.enrollement_module.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pfe.example.enrollement_module.entities.DemandeInscription;
import pfe.example.enrollement_module.enumerateur.StatutDemandeInscription;
import pfe.example.enrollement_module.repository.DemandeInscriptionRepository;

@Service
@RequiredArgsConstructor
public class DemandeInscriptionService {

    private final DemandeInscriptionRepository demandeInscriptionRepository;

    public DemandeInscription submitCandidature(DemandeInscription request) {

        // Création d'une nouvelle demande d'inscription
        DemandeInscription demande = new DemandeInscription();
        // Statut initial
        demande.setStatut(StatutDemandeInscription.SOUMIS);

        // Sauvegarde
        return demandeInscriptionRepository.save(demande);
    }
}
