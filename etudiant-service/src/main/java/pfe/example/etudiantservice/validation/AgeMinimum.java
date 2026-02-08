package pfe.example.etudiantservice.validation;

import jakarta.validation.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AgeMinimumValidator.class)
public @interface AgeMinimum {
    int value();
    String message() default "L'âge minimum est {value} ans";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

