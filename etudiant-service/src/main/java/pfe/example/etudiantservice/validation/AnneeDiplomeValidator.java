package pfe.example.etudiantservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pfe.example.etudiantservice.entities.Etudiant;

import java.time.LocalDate;

public class AnneeDiplomeValidator
        implements ConstraintValidator<AnneeDiplomeValide, Etudiant> {

    @Override
    public boolean isValid(Etudiant etudiant, ConstraintValidatorContext context) {

        // Si pas de diplôme → OK
        if (etudiant.getDernierDiplome() == null) {
            return true;
        }

        Integer anneeDiplome = etudiant.getAnneeDernierDiplome();
        LocalDate dateNaissance = etudiant.getDateNaissance();

        if (anneeDiplome == null || dateNaissance == null) {
            return true;
        }

        int anneeNaissance = dateNaissance.getYear();
        int anneeCourante = LocalDate.now().getYear();

        // ❌ Année future
        if (anneeDiplome > anneeCourante) {
            return false;
        }

        // ❌ Diplôme obtenu avant 18 ans
        int ageLorsDiplome = anneeDiplome - anneeNaissance;
        return ageLorsDiplome >= 18;
    }
}
