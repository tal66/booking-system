package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.exception.BookingValidationException;
import com.att.tdp.popcorn_palace.model.Booking;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repository.BookingRepository;
import com.att.tdp.popcorn_palace.service.interfaces.BookingService;
import com.att.tdp.popcorn_palace.service.interfaces.ShowtimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final ShowtimeService showtimeService;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, ShowtimeService showtimeService) {
        this.bookingRepository = bookingRepository;
        this.showtimeService = showtimeService;
    }

    @Override
    @Transactional
    public Optional<Booking> createBooking(Long showtimeId, Integer seatNumber, String userId) {

        // check showtime exists
        Optional<Showtime> showtimeOpt = showtimeService.getShowtimeById(showtimeId);
        if (showtimeOpt.isEmpty()) {
            logger.warn("Booking failed: Showtime with ID {} not found", showtimeId);
            throw new BookingValidationException("Showtime not found, ID: " + showtimeId);
        }
        Showtime showtime = showtimeOpt.get();

        // check seat num
        Integer numSeats = showtime.getTheater().getNumSeats();
        if (seatNumber < 1 || seatNumber > numSeats) {
            logger.warn("Booking failed: Invalid seat number {} for theater with {} seats",
                    seatNumber, numSeats);
            throw new BookingValidationException("Invalid seat number. Theater has " +
                    numSeats + " seats");
        }

        // check seat already booked
        if (isSeatBooked(showtimeId, seatNumber)) {
            logger.warn("Booking failed: Seat {}, showtime {} already booked", seatNumber, showtimeId);
            throw new BookingValidationException("Seat is already booked");
        }

        // create
        Booking booking = new Booking(showtime, seatNumber, userId);
        Booking savedBooking = bookingRepository.save(booking);
        logger.info("Created booking {} for showtime {}, seat {}, user {}",
                savedBooking.getId(), showtimeId, seatNumber, userId);

        return Optional.of(savedBooking);
    }

    @Override
    public Optional<Booking> getBookingById(UUID id) {
        return bookingRepository.findById(id);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public List<Booking> getBookingsByShowtimeId(Long showtimeId) {
        Optional<Showtime> showtime = showtimeService.getShowtimeById(showtimeId);
        return showtime.map(bookingRepository::findByShowtime).orElse(List.of());
    }

    @Override
    @Transactional
    public boolean cancelBooking(UUID id) {
        if (bookingRepository.existsById(id)) {
            bookingRepository.deleteById(id);
            logger.info("Canceled booking {}", id);
            return true;
        }
        logger.warn("Cancel booking failed: Booking {} not found", id);
        return false;
    }

    @Override
    public boolean isSeatBooked(Long showtimeId, Integer seatNumber) {
        return bookingRepository.existsByShowtimeIdAndSeatNumber(showtimeId, seatNumber);
    }

    @Override
    public List<Integer> getBookedSeats(Long showtimeId) {
        return bookingRepository.findBookedSeatsByShowtimeId(showtimeId);
    }
}