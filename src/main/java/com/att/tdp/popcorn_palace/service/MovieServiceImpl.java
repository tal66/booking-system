package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.service.interfaces.MovieService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Autowired
    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    @Cacheable(value = "allMovies")
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    @Caching(
            evict = { @CacheEvict(value = "allMovies", allEntries = true) }
    )
    public Optional<Movie> addMovie(Movie movie) {
        return Optional.of(movieRepository.save(movie));
    }

    @Override
    public Page<Movie> getAllMoviesPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.findAll(pageable);
    }

    @Override
    @Caching(
            evict = { @CacheEvict(value = "allMovies", allEntries = true) }
    )
    public Optional<Movie> updateMovie(String movieTitle, Movie updatedMovie) {
        Optional<Movie> existingMovie = movieRepository.findByTitle(movieTitle);

        if (existingMovie.isPresent()) {
            Movie movie = existingMovie.get();
            movie.setTitle(updatedMovie.getTitle());
            movie.setGenre(updatedMovie.getGenre());
            movie.setDuration(updatedMovie.getDuration());
            movie.setRating(updatedMovie.getRating());
            movie.setReleaseYear(updatedMovie.getReleaseYear());

            return Optional.of(movieRepository.save(movie));
        }

        return Optional.empty();
    }

    @Override
    @Transactional
    @Caching(
            evict = { @CacheEvict(value = "allMovies", allEntries = true) }
    )
    public boolean deleteMovie(String movieTitle) {
        Optional<Movie> movie = movieRepository.findByTitleIgnoreCase(movieTitle);
        if (movie.isPresent()) {
            movieRepository.deleteByTitleIgnoreCase(movieTitle);
            return true;
        }
        return false;
    }

    ////////// find

    @Override
    public Optional<Movie> findById(Long id) {
        return movieRepository.findById(id);
    }


    @Override
    public Optional<Movie> findByTitle(String title) {
        return movieRepository.findByTitleIgnoreCase(title);
    }

    @Override
    public List<Movie> findByGenre(String genre) {
        return movieRepository.findByGenre(genre);
    }

    @Override
    public List<Movie> findByRatingGreaterThanEqual(Double rating) {
        return movieRepository.findByRatingGreaterThanEqual(rating);
    }

    @Override
    public List<Movie> findByTitleAndGenreAndRatingGreaterThanEqual(String title, String genre, Double rating) {
        return movieRepository.findByTitleContainingIgnoreCaseAndGenreContainingIgnoreCaseAndRatingGreaterThanEqual(title, genre, rating);
    }
}
