package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.dto.BookingRequest;
import com.att.tdp.popcorn_palace.exception.BookingValidationException;
import com.att.tdp.popcorn_palace.model.Booking;
import com.att.tdp.popcorn_palace.service.interfaces.BookingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingRequest request) {
        logger.info("Booking request received for showtime: {}, seat: {}",
                request.getShowtimeId(), request.getSeatNumber());

        try {
            Optional<Booking> booking;

            booking = bookingService.createBooking(
                    request.getShowtimeId(),
                    request.getSeatNumber(),
                    request.getUserId()
            );

            if (booking.isPresent()) {
                Map<String, String> response = new HashMap<>();
                response.put("bookingId", booking.get().getId().toString());

                logger.info("Booking created successfully with ID: {}", booking.get().getId());
                return ResponseEntity.ok(response);
            } else {
                logger.error("Failed to create booking");

                Map<String, String> response = new HashMap<>();
                response.put("error", "Failed to create booking");

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (BookingValidationException e) {
            logger.warn("Booking validation failed: {}", e.getMessage());

            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable UUID bookingId) {
        logger.info("Request received to retrieve booking with ID: {}", bookingId);

        Optional<Booking> booking = bookingService.getBookingById(bookingId);

        if (booking.isPresent()) {
            logger.info("Booking found with ID: {}", bookingId);
            return ResponseEntity.ok(booking.get());
        } else {
            logger.warn("Booking not found with ID: {}", bookingId);

            Map<String, String> response = new HashMap<>();
            response.put("error", "Booking not found");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}