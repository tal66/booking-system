package com.att.tdp.popcorn_palace.service.interfaces;

import com.att.tdp.popcorn_palace.model.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    List<Movie> getAllMovies();

    Optional<Movie> addMovie(Movie movie);

    Optional<Movie> updateMovie(String movieTitle, Movie movie);

    boolean deleteMovie(String movieTitle);

    List<Movie> findByTitleAndGenreAndRatingGreaterThanEqual(String title, String genre, Double rating);
    Optional<Movie> findById(Long id);
    Optional<Movie> findByTitle(String title);
    List<Movie> findByGenre(String genre);
    List<Movie> findByRatingGreaterThanEqual(Double rating);


}
