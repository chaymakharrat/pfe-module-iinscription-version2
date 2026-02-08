package pfe.example.etudiantservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pfe.example.etudiantservice.dto.PaysDTO;
import pfe.example.etudiantservice.entities.Pays;
import pfe.example.etudiantservice.exception.ResourceNotFoundException;
import pfe.example.etudiantservice.mapper.PaysMapper;
import pfe.example.etudiantservice.repositories.PaysRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaysService {

    private final PaysRepository paysRepository;
    private final PaysMapper paysMapper;

    // Récupérer tous les pays avec seulement le nom
    public List<String> getAllPaysNoms() {
        return paysRepository.findAll()
                .stream()
                .map(Pays::getNom)
                .collect(Collectors.toList());
    }
    public PaysDTO getPaysByIndicatif(String indicatif) {
        Pays pays = paysRepository.findByIndicatif(indicatif)
                .orElseThrow(() -> new RuntimeException("Pays introuvable pour l’indicatif : " + indicatif));

        return PaysDTO.builder()
                .id(pays.getId())
                .nom(pays.getNom())
                .indicatif(pays.getIndicatif())
                .build();
    }


    // Récupérer tous les pays avec nom + indicatif
    public List<PaysDTO> getAllPaysNomIndicatif() {
        return paysRepository.findAll()
                .stream()
                .map(p -> PaysDTO.builder()
                        .id(p.getId())          // optionnel si tu veux l’id
                        .nom(p.getNom())
                        .indicatif(p.getIndicatif())
                        .build())
                .collect(Collectors.toList());
    }
    public PaysDTO getPaysById(Long id) {
        Pays pays = paysRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pays non trouvé avec l'id : " + id));
        return paysMapper.toDTO(pays);
    }

}
