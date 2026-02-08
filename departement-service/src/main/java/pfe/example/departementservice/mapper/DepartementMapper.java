package pfe.example.departementservice.mapper;

import pfe.example.departementservice.dto.*;
import pfe.example.departementservice.entities.*;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class DepartementMapper {

    public DepartementDTO toDto(Departement departement) {
        DepartementDTO dto = new DepartementDTO();
        dto.setId(departement.getId());
        dto.setNom(departement.getNom());
        if (departement.getDiplomeEtudiers() != null) {
            dto.setDiplomes(
                    departement.getDiplomeEtudiers().stream()
                            .map(this::toDiplomeDto)
                            .collect(Collectors.toSet())
            );
        }
        return dto;
    }
public DiplomeEtudierDTO toDiplomeDto(DiplomeEtudier diplome) {

    DiplomeEtudierDTO dto = new DiplomeEtudierDTO();
    dto.setId(diplome.getId());
    dto.setNom(diplome.getNom());
    dto.setFraisInscription(diplome.getFraisInscription());
    dto.setActif(diplome.isActif());

    if (diplome.getType() != null) {
        dto.setType(diplome.getType().getNom());

        // ✅ PREREQUIS À PARTIR DU TYPE
        dto.setPrerequis(
                diplome.getType().getPrerequis()
                        .stream()
                        .map(Prerequis::getNom)
                        .collect(Collectors.toSet())
        );
    }

    if (diplome.getDepartement() != null) {
        dto.setDepartementNom(diplome.getDepartement().getNom());
    }

    return dto;
}


    public EnseignantDTO toEnseignantDto(Enseignant enseignant) {
        EnseignantDTO dto = new EnseignantDTO();
        dto.setId(enseignant.getId());
        dto.setNom(enseignant.getNom());
        dto.setPrenom(enseignant.getPrenom());
        dto.setEmail(enseignant.getEmail());
        dto.setPhone(enseignant.getPhone());
        if (enseignant.getDiplomeEtudier() != null) dto.setDiplomeNom(enseignant.getDiplomeEtudier().getNom());
        return dto;
    }

    public NiveauDiplomeDTO toNiveauDto(NiveauDiplome niveau) {
        NiveauDiplomeDTO dto = new NiveauDiplomeDTO();
        dto.setId(niveau.getId());
        dto.setNiveau(niveau.getNiveau());
        return dto;
    }

    public NiveauDiplomeSpecifiqueDTO toNiveauSpecifiqueDto(Niveau_diplome_specifique ns) {
        NiveauDiplomeSpecifiqueDTO dto = new NiveauDiplomeSpecifiqueDTO();
        dto.setId(ns.getId());
        dto.setNiveau(ns.getNiveau().getNiveau());
        dto.setDiplome(ns.getDiplome().getNom()); // ou le nom seulement
        dto.setCapaciteMax(ns.getCapaciteMax());
        return dto;
    }
    public TypeDTO toTypeDto(Type type) {
        TypeDTO dto = new TypeDTO();
        dto.setId(type.getId());
        dto.setNom(type.getNom());

        if (type.getPrerequis() != null) {
            dto.setPrerequis(
                    type.getPrerequis()
                            .stream()
                            .map(Prerequis::getNom)
                            .collect(Collectors.toSet())
            );
        }
        return dto;
    }


}
