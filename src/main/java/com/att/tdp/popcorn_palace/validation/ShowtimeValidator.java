package com.att.tdp.popcorn_palace.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ShowtimeValidatorImpl.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ShowtimeValidator {
    String message() default "Showtime validation failed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}