package pfe.example.etudiantservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pfe.example.etudiantservice.entities.Etudiant;

public class CinOrPassportValidator implements ConstraintValidator<CinOrPassport, Etudiant> {

    @Override
    public boolean isValid(Etudiant e, ConstraintValidatorContext context) {

        boolean hasCin = e.getNumCarteIdentite() != null && !e.getNumCarteIdentite().isBlank();
        boolean hasPassport = e.getNumPassport() != null && !e.getNumPassport().isBlank();

        return hasCin || hasPassport; // ✅ AU MOINS UN DES DEUX
    }
}

