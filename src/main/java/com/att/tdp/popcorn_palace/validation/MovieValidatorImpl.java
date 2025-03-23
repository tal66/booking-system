package com.att.tdp.popcorn_palace.validation;

import com.att.tdp.popcorn_palace.model.Movie;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MovieValidatorImpl implements ConstraintValidator<MovieValidator, Movie> {

    private static final int maxReleaseYear = 2026;
    private static final int minReleaseYear = 1888;

    @Override
    public boolean isValid(Movie movie, ConstraintValidatorContext context) {
        if (movie == null) return true;

        if (movie.getReleaseYear() != null && movie.getReleaseYear() > maxReleaseYear) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Release year must be at most " + maxReleaseYear)
                    .addPropertyNode("releaseYear")
                    .addConstraintViolation();
            return false;
        } else if (movie.getReleaseYear() != null && movie.getReleaseYear() < minReleaseYear) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Release year must be at least " + minReleaseYear)
                    .addPropertyNode("releaseYear")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}

