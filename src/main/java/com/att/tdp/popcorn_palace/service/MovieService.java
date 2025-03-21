package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.model.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    List<Movie> getAllMovies();

    Movie addMovie(Movie movie);

    Optional<Movie> updateMovie(String movieTitle, Movie movie);

    boolean deleteMovie(String movieTitle);

    List<Movie> findByTitleAndGenreAndRatingGreaterThanEqual(String title, String genre, Double rating);
    List<Movie> findByTitleContaining(String title);
    List<Movie> findByGenre(String genre);
    List<Movie> findByRatingGreaterThanEqual(Double rating);


}
