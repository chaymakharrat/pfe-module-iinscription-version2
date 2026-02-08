package pfe.example.etudiantservice.mapper;

import org.springframework.stereotype.Component;
import pfe.example.etudiantservice.dto.PaysDTO;
import pfe.example.etudiantservice.entities.Pays;


@Component
public class PaysMapper {

    public PaysDTO toDTO(Pays pays) {
        if (pays == null) return null;
        return PaysDTO.builder()
                .id(pays.getId())
                .nom(pays.getNom())
                .indicatif(pays.getIndicatif())
                .build();
    }

    public Pays toEntity(PaysDTO dto) {
        if (dto == null) return null;
        Pays pays = new Pays();
        pays.setId(dto.getId());
        pays.setNom(dto.getNom());
        pays.setIndicatif(dto.getIndicatif());
        return pays;
    }
}
