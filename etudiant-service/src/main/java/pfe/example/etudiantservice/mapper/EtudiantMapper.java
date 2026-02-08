package pfe.example.etudiantservice.mapper;

import org.springframework.stereotype.Component;
import pfe.example.etudiantservice.dto.EtudiantDTO;
import pfe.example.etudiantservice.entities.Etudiant;
import pfe.example.etudiantservice.entities.Pays;

@Component
public class EtudiantMapper {

    public EtudiantDTO toDTO(Etudiant etudiant) {
        if (etudiant == null) {
            return null;
        }

        return EtudiantDTO.builder()
                .id(etudiant.getId())
                .matricule(etudiant.getMatricule())
                .nom(etudiant.getNom())
                .prenom(etudiant.getPrenom())
                .email(etudiant.getEmail())
                .phone(etudiant.getPhone())
                .genre(etudiant.getGenre())
                .dernierDiplome(etudiant.getDernierDiplome())
                .anneeDernierDiplome(etudiant.getAnneeDernierDiplome())
                .dateNaissance(etudiant.getDateNaissance())
                .numCarteIdentite(etudiant.getNumCarteIdentite())
                .numPassport(etudiant.getNumPassport())
                .paysNom(etudiant.getPays() != null ? etudiant.getPays().getNom() : null)
                .dateInscription(etudiant.getDateInscription())
                .hasAllRequiredDocuments(etudiant.hasAllRequiredDocuments())
                .build();
    }

    public Etudiant toEntity(EtudiantRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Etudiant etudiant = new Etudiant();
        etudiant.setNom(dto.getNom());
        etudiant.setPrenom(dto.getPrenom());
        etudiant.setEmail(dto.getEmail());
        etudiant.setPhone(dto.getPhone());
        etudiant.setGenre(dto.getGenre());
        etudiant.setDernierDiplome(dto.getDernierDiplome());
        etudiant.setAnneeDernierDiplome(dto.getAnneeDernierDiplome());
        etudiant.setDateNaissance(dto.getDateNaissance());
        etudiant.setNumCarteIdentite(dto.getNumCarteIdentite());
        etudiant.setNumPassport(dto.getNumPassport());



        if (dto.getPaysId() != null) {
            Pays pays = new Pays();
            pays.setId(dto.getPaysId());
            etudiant.setPays(pays);
        }

        return etudiant;
    }
}