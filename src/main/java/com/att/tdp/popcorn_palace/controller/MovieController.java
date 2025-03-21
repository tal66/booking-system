package com.att.tdp.popcorn_palace.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;
    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);


    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieService.getAllMovies();

        logger.info("all movies ({})", movies.size());
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Movie> addMovie(@Valid @RequestBody Movie movie) {
        Movie savedMovie = movieService.addMovie(movie);

        logger.info("added movie '{}'", savedMovie.getTitle());

        return new ResponseEntity<>(savedMovie, HttpStatus.OK);
    }

    @PostMapping("/update/{movieTitle}")
    public ResponseEntity<Map<String, Object>> updateMovie(
            @PathVariable String movieTitle,
            @Valid @RequestBody Movie movie) {

        Optional<Movie> updatedMovie = movieService.updateMovie(movieTitle, movie);
        Map<String, Object> response = new HashMap<>();

        if (updatedMovie.isPresent()) {
            logger.info("updated movie '{}'", updatedMovie.get().getTitle());

            response.put("movie", updatedMovie.get().getTitle());

            return ResponseEntity.ok(response);
        } else {
            logger.info("failed to update movie '{}'", movieTitle);

            response.put("movie", movieTitle);
            response.put("error", "Movie not found");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{movieTitle}")
    public ResponseEntity<Map<String, Object>> deleteMovie(@PathVariable String movieTitle) {
        boolean deleted = movieService.deleteMovie(movieTitle);
        Map<String, Object> response = new HashMap<>();
        response.put("movie", movieTitle);

        if (deleted) {
            logger.info("deleted movie '{}'", movieTitle);
            return ResponseEntity.ok(response);
        } else {
            logger.info("failed to delete movie '{}'", movieTitle);
            response.put("error", "Movie not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<Movie>> searchMovies(
            @RequestParam(required = false, defaultValue = "") String title,
            @RequestParam(required = false, defaultValue = "") String genre,
            @RequestParam(required = false, defaultValue = "0.0") Double rating) {

        List<Movie> movies = movieService.findByTitleAndGenreAndRatingGreaterThanEqual(title, genre, rating);

        logger.info("search movies (title: '{}', genre: '{}', rating: '{}'). res size: {})", title, genre, rating, movies.size());
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
//        var errors = ex.getBindingResult()
//                .getAllErrors()
//                .stream()
//                .collect(
//                        java.util.stream.Collectors.toMap(
//                                error -> ((FieldError) error).getField(),
//                                error -> error.getDefaultMessage(),
//                                (error1, error2) -> error1
//                        )
//                );
//
//        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
//    }
}