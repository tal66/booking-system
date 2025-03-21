package com.att.tdp.popcorn_palace.repository;

import com.att.tdp.popcorn_palace.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    // rm
    void deleteByTitle(String title);

    // search
    Optional<Movie> findByTitle(String title);

    List<Movie> findByTitleContaining(String title);
    List<Movie> findByGenre(String genre);
    List<Movie> findByRatingGreaterThanEqual(Double rating);

    List<Movie> findByTitleContainingIgnoreCaseAndGenreContainingIgnoreCaseAndRatingGreaterThanEqual(String title, String genre, Double rating);

}
