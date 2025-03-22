package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.exception.ShowtimeValidationException;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import com.att.tdp.popcorn_palace.service.interfaces.ShowtimeService;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShowtimeServiceImpl implements ShowtimeService {

    private static final Logger logger = LoggerFactory.getLogger(ShowtimeServiceImpl.class);
    private final ShowtimeRepository showtimeRepository;

    @Autowired
    public ShowtimeServiceImpl(ShowtimeRepository showtimeRepository, Validator validator) {
        this.showtimeRepository = showtimeRepository;
    }

    @Override
    public List<Showtime> getAllShowtimes() {
        return showtimeRepository.findAll();
    }

    @Override
    public Optional<Showtime> getShowtimeById(Long id) {
        return showtimeRepository.findById(id);
    }

    @Override
    public Optional<Showtime> addShowtime(Showtime showtime) {
        return saveShowtime(showtime);
    }

    private Optional<Showtime> saveShowtime(Showtime showtime) {
        validate(showtime);
        return Optional.of(showtimeRepository.save(showtime));
    }

    @Override
    public Optional<Showtime> updateShowtime(Long id, Showtime updatedShowtime) {
        Optional<Showtime> existingShowtime = showtimeRepository.findById(id);

        if (existingShowtime.isPresent()) {
            Showtime showtime = existingShowtime.get();
            showtime.setMovie(updatedShowtime.getMovie());
            showtime.setPrice(updatedShowtime.getPrice());
            showtime.setTheater(updatedShowtime.getTheater());
            showtime.setStartTime(updatedShowtime.getStartTime());
            showtime.setEndTime(updatedShowtime.getEndTime());

//            validate(showtime);
            return saveShowtime(showtime);
        }

        return Optional.empty();
    }

    @Override
    @Transactional
    public boolean deleteShowtime(Long id) {
        if (showtimeRepository.existsById(id)) {
            showtimeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<Showtime> findByMovie(Movie movie) {
        return showtimeRepository.findByMovie(movie);
    }

    @Override
    public List<Showtime> findByMovieId(Long movieId) {
        return showtimeRepository.findByMovie_Id(movieId);
    }

    //// Validation methods

    private void validate(Showtime showtime) {
        Pair<Boolean, String> validationOverlapResult = validateNoOverlapShowtime(showtime);
        if (!validationOverlapResult.getFirst()) {
            throw new ShowtimeValidationException(validationOverlapResult.getSecond());
        }
    }

    private Pair<Boolean, String> validateNoOverlapShowtime(Showtime showtime) {
        // If any required fields are null, not checking for overlaps (other validators handle)
        if (showtime.getTheater() == null || showtime.getStartTime() == null || showtime.getEndTime() == null) {
            return Pair.of(true, "");
        }

        logger.info("theater id: {}, start time: {}, end time: {}", showtime.getTheater().getId(), showtime.getStartTime(), showtime.getEndTime());

        // fetch potentially overlapping showtimes
        List<Showtime> potentialOverlaps = showtimeRepository.findOverlappingShowtimes(
                showtime.getTheater().getId(),
                showtime.getStartTime(),
                showtime.getEndTime());

        if (showtime.getId() != null) {
            potentialOverlaps.removeIf(s -> s.getId().equals(showtime.getId()));
        }
        logger.info("potential overlaps size: {}", potentialOverlaps.size());

        if (!potentialOverlaps.isEmpty()) {
            Showtime first = potentialOverlaps.getFirst();
            return Pair.of(false, "Showtime overlaps with existing showtime (id: " + first.getId() + ", times: " + first.getStartTime() + " - " + first.getEndTime() + ")");
        }

        return Pair.of(true, "");
    }
}