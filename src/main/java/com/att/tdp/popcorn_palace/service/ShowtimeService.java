package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;

import java.util.List;
import java.util.Optional;

public interface ShowtimeService {
    List<Showtime> getAllShowtimes();
    Optional<Showtime> getShowtimeById(Long id);
    Optional<Showtime> addShowtime(Showtime showtime);
    Optional<Showtime> updateShowtime(Long id, Showtime showtime);
    boolean deleteShowtime(Long id);
    List<Showtime> findByMovie(Movie movie);
    List<Showtime> findByMovieId(Long movieId);
}