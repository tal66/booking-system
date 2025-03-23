package com.att.tdp.popcorn_palace.validation;

import com.att.tdp.popcorn_palace.model.Showtime;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;


public class ShowtimeValidatorImpl implements ConstraintValidator<ShowtimeValidator, Showtime> {
    private static final Logger logger = LoggerFactory.getLogger(ShowtimeValidatorImpl.class);
    private int maxDurationMinutes;
    private int minDurationMinutes;

    @Override
    public void initialize(ShowtimeValidator constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.maxDurationMinutes = constraintAnnotation.maxDurationMinutes();
        this.minDurationMinutes = constraintAnnotation.minDurationMinutes();
    }

    @Override
    public boolean isValid(Showtime showtime, ConstraintValidatorContext context) {
        boolean isValid = true;

        // Disable default message
        context.disableDefaultConstraintViolation();

        // Validate start time is before end time
        isValid = validateStartBeforeEndTime(showtime, context);
        if (!isValid) {
            return false;
        }

        // Validate duration is less than max duration
        isValid = validateDuration(showtime, context);
        if (!isValid) {
            return false;
        }

        return true;
    }

    private boolean validateDuration(Showtime showtime, ConstraintValidatorContext context) {
        if (showtime.getStartTime() == null || showtime.getEndTime() == null) {
            // Let the @NotNull annotations handle null values
            return true;
        }

        long durationMinutes = Duration.between(showtime.getStartTime(), showtime.getEndTime()).toMinutes();
        boolean isDurationValid = (durationMinutes <= maxDurationMinutes);
        isDurationValid = (durationMinutes >= minDurationMinutes) && isDurationValid;

        if (!isDurationValid) {
            context.buildConstraintViolationWithTemplate(
                            "Showtime duration must between " + minDurationMinutes + " - " + maxDurationMinutes + " minutes")
                    .addPropertyNode("endTime")
                    .addConstraintViolation();
        }

        return isDurationValid;
    }


    private boolean validateStartBeforeEndTime(Showtime showtime, ConstraintValidatorContext context) {
        if (showtime.getStartTime() == null || showtime.getEndTime() == null) {
            // Let the @NotNull annotations handle null values
            return true;
        }

        boolean isTimeValid = showtime.getEndTime().isAfter(showtime.getStartTime());

        if (!isTimeValid) {
            context.buildConstraintViolationWithTemplate("End time must be after start time")
                    .addPropertyNode("endTime")
                    .addConstraintViolation();
        }

        return isTimeValid;
    }

}