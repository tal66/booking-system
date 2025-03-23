package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.dto.ShowtimeRequest;
import com.att.tdp.popcorn_palace.exception.ShowtimeValidationException;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.model.Theater;
import com.att.tdp.popcorn_palace.service.interfaces.MovieService;
import com.att.tdp.popcorn_palace.service.interfaces.ShowtimeService;
import com.att.tdp.popcorn_palace.service.interfaces.TheaterService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/showtimes")
public class ShowtimeController {

    private final ShowtimeService showtimeService;
    private final MovieService movieService;
    private final TheaterService theaterService;
    private static final Logger logger = LoggerFactory.getLogger(ShowtimeController.class);

    @Autowired
    public ShowtimeController(ShowtimeService showtimeService, MovieService movieService, TheaterService theaterService) {
        this.showtimeService = showtimeService;
        this.movieService = movieService;
        this.theaterService = theaterService;
    }

    @GetMapping("/{showtimeId}")
    public ResponseEntity<?> getShowtimeById(@PathVariable Long showtimeId) {
        Optional<Showtime> showtime = showtimeService.getShowtimeById(showtimeId);

        if (showtime.isPresent()) {
            logger.info("found showtime with id: {}", showtimeId);
            return new ResponseEntity<>(showtime.get(), HttpStatus.OK);
        } else {
            logger.info("showtime not found with id: {}", showtimeId);
            HashMap<String, Object> response = new HashMap<>();
            response.put("error", "Showtime not found");
            response.put("id", showtimeId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // Updated to handle movieId in request body
    @PostMapping
    public ResponseEntity<?> addShowtime(@Valid @RequestBody ShowtimeRequest request) {
        // movie
        Long reqMovieId = request.getMovieId();
        Optional<Movie> movie = movieService.findById(reqMovieId);
        if (movie.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Movie not found with id: " + reqMovieId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // theater
        String theaterName = request.getTheater();
        Optional<Theater> theaterOptional = theaterService.findByName(theaterName);
        if (theaterOptional.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Theater not found with name: " + theaterName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        Theater theater = theaterOptional.get();

        // new showtime
        Showtime showtime = new Showtime();
        showtime.setMovie(movie.get());
        showtime.setPrice(request.getPrice());
        showtime.setTheater(theater);
        showtime.setStartTime(request.getStartTime());
        showtime.setEndTime(request.getEndTime());


        logger.info("saving showtime for movie: '{}'", movie.get().getTitle());
        Optional<Showtime> optSavedShowtime;
        try {
            optSavedShowtime = showtimeService.addShowtime(showtime);
        } catch (ShowtimeValidationException e) {
            logger.error("failed to add showtime (ShowtimeValidationException). movie: '{}'", movie.get().getTitle());
            Map<String, Object> response = Map.of("error", "Failed to add showtime. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (optSavedShowtime.isEmpty()) {
            logger.error("failed to add showtime (save failed). movie: '{}'", movie.get().getTitle());

            Map<String, Object> response = new HashMap<>();
            response.put("error", "Failed to add showtime");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        Showtime savedShowtime = optSavedShowtime.get();

        logger.info("added showtime for movie: '{}' at theater: {}",
                savedShowtime.getMovie().getTitle(), savedShowtime.getTheater());

        return new ResponseEntity<>(savedShowtime, HttpStatus.OK);
    }

    @PostMapping("/update/{showtimeId}")
    public ResponseEntity<?> updateShowtime(
            @PathVariable Long showtimeId,
            @Valid @RequestBody ShowtimeRequest request) {

        logger.info("updating showtime with id: {} ...", showtimeId);
        try {
            // check showtime exists
            Optional<Showtime> existingShowtime = showtimeService.getShowtimeById(showtimeId);
            if (existingShowtime.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("id", showtimeId);
                response.put("error", "Showtime not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // movieId
            Long movieId = request.getMovieId();
            Optional<Movie> movie = movieService.findById(movieId);

            if (movie.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Movie not found with ID: " + movieId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // theater
            String theaterName = request.getTheater();
            Optional<Theater> theaterOptional = theaterService.findByName(theaterName);
            if (theaterOptional.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Theater not found with name: " + theaterName);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // update showtime
            Showtime showtime = existingShowtime.get();
            showtime.setMovie(movie.get());
            showtime.setPrice(request.getPrice());
            showtime.setTheater(theaterOptional.get());
            showtime.setStartTime(java.time.Instant.parse(request.getStartTime().toString()));
            showtime.setEndTime(java.time.Instant.parse(request.getEndTime().toString()));

            logger.info("updating showtime for movie: '{}'", movie.get().getTitle());
            Optional<Showtime> optUpdatedShowtime = showtimeService.updateShowtime(showtimeId, showtime);

            if (optUpdatedShowtime.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("id", showtimeId);
                response.put("error", "Failed to update showtime");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

            Showtime updatedShowtime = optUpdatedShowtime.get();
            Map<String, Object> response = new HashMap<>();
            response.put("id", updatedShowtime.getId());
            response.put("message", "Showtime updated successfully");

            logger.info("updated showtime with id: {}", showtimeId);
            return ResponseEntity.ok(response);
        } catch (ShowtimeValidationException e) {
            logger.error("failed to update showtime {}. error: {}", showtimeId, e.getMessage());
            Map<String, Object> response = Map.of("error", "Failed to update showtime. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{showtimeId}")
    public ResponseEntity<Map<String, Object>> deleteShowtime(@PathVariable Long showtimeId) {
        boolean deleted = showtimeService.deleteShowtime(showtimeId);

        Map<String, Object> response = new HashMap<>();
        response.put("id", showtimeId);

        if (deleted) {
            logger.info("deleted showtime with id: {}", showtimeId);
            response.put("message", "Showtime deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            logger.info("failed to delete showtime with id: {}", showtimeId);
            response.put("error", "Showtime not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<Showtime>> getAllShowtimes() {
        List<Showtime> showtimes = showtimeService.getAllShowtimes();

        logger.info("retrieved all showtimes ({})", showtimes.size());
        return new ResponseEntity<>(showtimes, HttpStatus.OK);
    }

}