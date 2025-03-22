package com.att.tdp.popcorn_palace.repository;

import com.att.tdp.popcorn_palace.model.Booking;
import com.att.tdp.popcorn_palace.model.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findByShowtime(Showtime showtime);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.showtime.id = :showtimeId AND b.seatNumber = :seatNumber")
    boolean existsByShowtimeIdAndSeatNumber(@Param("showtimeId") Long showtimeId, @Param("seatNumber") Integer seatNumber);

    @Query("SELECT b.seatNumber FROM Booking b " +
            "WHERE b.showtime.id = :showtimeId")
    List<Integer> findBookedSeatsByShowtimeId(@Param("showtimeId") Long showtimeId);
}