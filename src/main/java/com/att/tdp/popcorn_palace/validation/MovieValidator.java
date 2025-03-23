package com.att.tdp.popcorn_palace.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE}) // Apply to class level
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MovieValidatorImpl.class)
public @interface MovieValidator {
    String message() default "Invalid movie details";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
