package pfe.example.etudiantservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class AgeMinimumValidator implements ConstraintValidator<AgeMinimum, LocalDate> {

    private int ageMin;

    @Override
    public void initialize(AgeMinimum constraintAnnotation) {
        this.ageMin = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(LocalDate dateNaissance, ConstraintValidatorContext context) {
        if (dateNaissance == null) return false;
        return Period.between(dateNaissance, LocalDate.now()).getYears() >= ageMin;
    }
}

