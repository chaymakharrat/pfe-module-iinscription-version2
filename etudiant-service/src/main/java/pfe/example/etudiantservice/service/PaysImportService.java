package pfe.example.etudiantservice.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pfe.example.etudiantservice.dto.PaysApiResponse;
import pfe.example.etudiantservice.entities.Pays;
import pfe.example.etudiantservice.repositories.PaysRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaysImportService {

    private final PaysRepository paysRepository;
    private final RestTemplate restTemplate;

    private static final String API_URL = "https://iso.lahrim.fr/countries";

    @Transactional
    public void importPays() {
        log.info("Début de l'import des pays...");

        PaysApiResponse response =
                restTemplate.getForObject(API_URL, PaysApiResponse.class);

        if (response == null || response.getData() == null) {
            log.warn("Réponse API vide");
            return;
        }

        log.info("Nombre de pays reçus: {}", response.getData().size());

        // Filtrer et transformer
        List<Pays> nouveauxPays = response.getData().stream()
                .filter(p -> {
                    boolean valid = p.getNom() != null && p.getIndicatif() != null;
                    if (!valid) {
                        log.warn("Pays ignoré (données nulles): {}", p);
                    }
                    return valid;
                })
                .filter(p -> {
                    boolean exists = paysRepository.existsByNom(p.getNom());
                    if (exists) {
                        log.debug("Pays déjà existant: {}", p.getNom());
                    }
                    return !exists;
                })
                .map(p -> {
                    Pays pays = new Pays();
                    pays.setNom(p.getNom());
                    pays.setIndicatif("+" + p.getIndicatif());
                    log.debug("Pays à créer: {} - {}", pays.getNom(), pays.getIndicatif());
                    return pays;
                })
                .collect(Collectors.toList());

        log.info("Nombre de nouveaux pays à sauvegarder: {}", nouveauxPays.size());

        if (!nouveauxPays.isEmpty()) {
            List<Pays> saved = paysRepository.saveAll(nouveauxPays);
            log.info("Pays sauvegardés avec succès: {}", saved.size());
        } else {
            log.info("Aucun nouveau pays à sauvegarder");
        }

        // Vérification finale
        long totalInDb = paysRepository.count();
        log.info("Nombre total de pays en base: {}", totalInDb);
    }
}