package com.att.tdp.popcorn_palace.validation;

import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;


import java.util.List;

//@Component
public class ShowtimeValidatorImpl implements ConstraintValidator<ShowtimeValidator, Showtime> {
    private static final Logger logger = LoggerFactory.getLogger(ShowtimeValidatorImpl.class);

//    @Autowired
//    private ShowtimeRepository showtimeRepository;

    @Override
    public void initialize(ShowtimeValidator constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
//        showtimeRepository = ServiceUtils.getShowtimeRepository(); // autowire not working!
    }

    @Override
    public boolean isValid(Showtime showtime, ConstraintValidatorContext context) {
        boolean isValid = true;

        // Disable default message
        context.disableDefaultConstraintViolation();

        // Validate start time is before end time
        isValid = validateStartEndTime(showtime, context);
        if (!isValid) {
            return false;
        }

        // Validate no theater showtime overlaps
//        isValid = validateNoTheaterOverlap(showtime, context);
//        if (!isValid) {
//            return false;
//        }

        return true;
    }

    private boolean validateStartEndTime(Showtime showtime, ConstraintValidatorContext context) {
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