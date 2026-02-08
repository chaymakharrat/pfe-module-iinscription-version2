package pfe.example.etudiantservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CinOrPassportValidator.class)
public @interface CinOrPassport {
    String message() default "Vous devez fournir soit le CIN soit le numéro de passeport";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

