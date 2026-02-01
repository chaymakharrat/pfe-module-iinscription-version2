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
        demande.setNom(request.getNom());
        demande.setPrenom(request.getPrenom());
        demande.setEmail(request.getEmail());
        demande.setPhone(request.getPhone());
        demande.setDateNaissance(request.getDateNaissance());
        demande.setGenre(request.getGenre());
        demande.setDernierDiplome(request.getDernierDiplome());
        demande.setAnneeDernierDiplome(request.getAnneeDernierDiplome());

        // Statut initial
        demande.setStatut(StatutDemandeInscription.SOUMIS);

        // Sauvegarde
        return demandeInscriptionRepository.save(demande);
    }
}
