package com.att.tdp.popcorn_palace.service.interfaces;

import com.att.tdp.popcorn_palace.model.Booking;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingService {


//    Optional<Booking> createBooking(Long showtimeId, Integer seatNumber);

    Optional<Booking> createBooking(Long showtimeId, Integer seatNumber, UUID userId);

    Optional<Booking> getBookingById(UUID id);

    List<Booking> getAllBookings();

    List<Booking> getBookingsByShowtimeId(Long showtimeId);

    boolean cancelBooking(UUID id);

    boolean isSeatBooked(Long showtimeId, Integer seatNumber);

    List<Integer> getBookedSeats(Long showtimeId);
}