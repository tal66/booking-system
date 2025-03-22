package com.att.tdp.popcorn_palace.repository;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    List<Showtime> findByMovie(@NonNull Movie movie);
    List<Showtime> findByMovie_Id(@NonNull Long movieId);
    List<Showtime> findByTheaterId(Long theaterId);

    @Query("SELECT s FROM Showtime s " +
            "WHERE s.theater.id = :theaterId AND :startTime < s.endTime AND :endTime > s.startTime")
    List<Showtime> findOverlappingShowtimes(
            @Param("theaterId") Long theaterId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime);
}