package pfe.example.etudiantservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AnneeDiplomeValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AnneeDiplomeValide {

    String message() default
            "L'année du dernier diplôme est invalide par rapport à la date de naissance";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
