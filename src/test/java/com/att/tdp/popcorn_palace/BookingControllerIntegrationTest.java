package com.att.tdp.popcorn_palace;

import com.att.tdp.popcorn_palace.dto.BookingRequest;
import com.att.tdp.popcorn_palace.dto.ShowtimeRequest;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.model.Theater;
import com.att.tdp.popcorn_palace.repository.BookingRepository;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import com.att.tdp.popcorn_palace.repository.TheaterRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
//@Transactional
public class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestDatabaseUtility testDatabaseUtility;

    private Movie testMovie;
    private Theater testTheater;
    private Showtime testShowtime;

    @BeforeEach
    public void setup() throws Exception {
        // Clean databases
        testDatabaseUtility.cleanDatabase();

        // Create test movie
        testMovie = new Movie("Inception", "Sci-Fi", 148, 8.8, 2010);
        testMovie = movieRepository.save(testMovie);

        // Create test theater
        testTheater = new Theater("Theater 1", 150);
        testTheater = theaterRepository.save(testTheater);

        // Create test showtime
        Instant startTime = Instant.now().plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS);
        Instant endTime = startTime.plus(148, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.SECONDS);

        ShowtimeRequest showtimeRequest = new ShowtimeRequest();
        showtimeRequest.setMovieId(testMovie.getId());
        showtimeRequest.setTheater(testTheater.getName());
        showtimeRequest.setPrice(12.99);
        showtimeRequest.setStartTime(startTime);
        showtimeRequest.setEndTime(endTime);

        // Add showtime
        MvcResult addResult = mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtimeRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Extract the showtime
        testShowtime = objectMapper.readValue(
                addResult.getResponse().getContentAsString(), Showtime.class);
    }

    @Test
    public void testCreateBookingSuccessfully() throws Exception {
        // Create booking request
        int seatNumber = 4;
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setShowtimeId(testShowtime.getId());
        bookingRequest.setSeatNumber(seatNumber);
        bookingRequest.setUserId(UUID.randomUUID());

        // Send booking request
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").exists());

        // Verify database state - booking should be added
        assertThat(bookingRepository.findAll()).hasSize(1);
        assertThat(bookingRepository.existsByShowtimeIdAndSeatNumber(testShowtime.getId(), seatNumber)).isTrue();
    }

    @Test
    public void testCreateBookingForAlreadyBookedSeat() throws Exception {
        // Create and save first booking
        BookingRequest firstBookingRequest = new BookingRequest();
        firstBookingRequest.setShowtimeId(testShowtime.getId());
        firstBookingRequest.setSeatNumber(75);
        firstBookingRequest.setUserId(UUID.randomUUID());

        // Send first booking request
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstBookingRequest)))
                .andExpect(status().isOk());

        // Create second booking for the same seat
        BookingRequest secondBookingRequest = new BookingRequest();
        secondBookingRequest.setShowtimeId(testShowtime.getId());
        secondBookingRequest.setSeatNumber(75); // Same seat number
        secondBookingRequest.setUserId(UUID.randomUUID()); // Different user

        // Attempt to book already booked seat
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondBookingRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsStringIgnoringCase("seat is already booked")));

        // Verify no additional booking was added
        assertThat(bookingRepository.findAll()).hasSize(1);
    }
}