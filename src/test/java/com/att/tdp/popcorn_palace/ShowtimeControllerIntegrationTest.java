package com.att.tdp.popcorn_palace;

import com.att.tdp.popcorn_palace.dto.ShowtimeRequest;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.model.Theater;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import com.att.tdp.popcorn_palace.repository.TheaterRepository;
import com.att.tdp.popcorn_palace.TestDatabaseUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
//@Transactional
public class ShowtimeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private TestDatabaseUtility testDatabaseUtility;

    @Autowired
    private ObjectMapper objectMapper;

    private Movie testMovie;
    private Theater testTheater;

    @BeforeEach
    public void setup() {
        // Clean databases
        testDatabaseUtility.cleanDatabase();

        // Create test movie
        testMovie = new Movie("Inception", "Sci-Fi", 148, 8.8, 2010);
        testMovie = movieRepository.save(testMovie);

        // Create test theater
        testTheater = new Theater("Theater 1", 150);
        testTheater = theaterRepository.save(testTheater);
    }

    @Test
    public void testAddShowtimeAndGetById() throws Exception {
        // Create test data
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
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.movie").value(testMovie.getTitle()))
                .andExpect(jsonPath("$.theater").value(testTheater.getName()))
                .andExpect(jsonPath("$.price").value(12.99))
                .andReturn();

        // Extract the showtime ID
        Showtime savedShowtime = objectMapper.readValue(
                addResult.getResponse().getContentAsString(), Showtime.class);

        // Get showtime by ID
        mockMvc.perform(get("/showtimes/{showtimeId}", savedShowtime.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedShowtime.getId()))
                .andExpect(jsonPath("$.movie").value(testMovie.getTitle()))
                .andExpect(jsonPath("$.price").value(12.99));

        // Verify database state
        assertThat(showtimeRepository.findAll()).hasSize(1);
    }

    @Test
    public void testAddShowtimeWithStartTimeAfterEndTime() throws Exception {
        // Create test data with invalid times (start time after end time)
        Instant endTime = Instant.now().plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS);
        Instant startTime = endTime.plus(30, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.SECONDS);

        ShowtimeRequest showtimeRequest = new ShowtimeRequest();
        showtimeRequest.setMovieId(testMovie.getId());
        showtimeRequest.setTheater(testTheater.getName());
        showtimeRequest.setPrice(12.99);
        showtimeRequest.setStartTime(startTime);
        showtimeRequest.setEndTime(endTime);

        // Attempt to add invalid showtime
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtimeRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsStringIgnoringCase("end time must be after start time")));


        // Verify database state - no showtime should be added
        assertThat(showtimeRepository.findAll()).hasSize(0);
    }

    @Test
    public void testAddShowtimeWithoutParams() throws Exception {
        // Create test data with missing parameters
        ShowtimeRequest showtimeRequest = new ShowtimeRequest();

        // Attempt to add showtime with missing parameters
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtimeRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsStringIgnoringCase("validation failed")));

        // Verify database state - no showtime should be added
        assertThat(showtimeRepository.findAll()).hasSize(0);
    }

    @Test
    public void testAddShowtimeWithOverlap() throws Exception {
        // First, create and save a showtime
        Instant startTime1 = Instant.now().plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS);
        Instant endTime1 = startTime1.plus(120, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.SECONDS);

        ShowtimeRequest showtimeRequest1 = new ShowtimeRequest(testMovie.getId(), 12.99, testTheater.getName(), startTime1, endTime1);

        // Add first showtime
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtimeRequest1)))
                .andExpect(status().isOk());

        // Verify the first showtime was added
        assertThat(showtimeRepository.findAll()).hasSize(1);

        // Now try to add an overlapping showtime
        // Create second showtime that starts during the first one
        Instant startTime2 = startTime1.plus(30, ChronoUnit.MINUTES);
        Instant endTime2 = endTime1.plus(30, ChronoUnit.MINUTES);

        ShowtimeRequest showtimeRequest2 = new ShowtimeRequest(testMovie.getId(), 14.99, testTheater.getName(), startTime2, endTime2);

        // Attempt to add overlapping showtime
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtimeRequest2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsStringIgnoringCase("overlap")));

        // Verify no additional showtime was added
        assertThat(showtimeRepository.findAll()).hasSize(1);
    }

    @Test
    public void testUpdateShowtimeWithOverlap() throws Exception {
        // First, create and save a showtime
        Instant startTime1 = Instant.now().plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS);
        Instant endTime1 = startTime1.plus(120, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.SECONDS);

        ShowtimeRequest showtimeRequest1 = new ShowtimeRequest(testMovie.getId(), 12.99, testTheater.getName(), startTime1, endTime1);

        // Add first showtime
        MvcResult addResult = mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtimeRequest1)))
                .andExpect(status().isOk())
                .andReturn();

        // Extract the showtime ID
        Showtime savedShowtime = objectMapper.readValue(
                addResult.getResponse().getContentAsString(), Showtime.class);

        // Verify the first showtime was added
        assertThat(showtimeRepository.findAll()).hasSize(1);

        // Create second showtime
        Instant startTime2 = startTime1.plus(150, ChronoUnit.MINUTES);
        Instant endTime2 = endTime1.plus(150, ChronoUnit.MINUTES);
        ShowtimeRequest showtimeRequest2 = new ShowtimeRequest(testMovie.getId(), 14.99, testTheater.getName(), startTime2, endTime2);

        // Add showtime that will later be updated to overlap
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtimeRequest2)))
                .andExpect(status().isOk());

        // Verify second showtime was added
        assertThat(showtimeRepository.findAll()).hasSize(2);

        // Update first showtime to overlap with the second one
        ShowtimeRequest updateRequest = new ShowtimeRequest(testMovie.getId(), 12.99, testTheater.getName(),
                startTime2.minus(30, ChronoUnit.MINUTES), endTime2.minus(30, ChronoUnit.MINUTES));

        // Attempt to update showtime with overlap
        mockMvc.perform(post("/showtimes/update/{showtimeId}", savedShowtime.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsStringIgnoringCase("overlap")));
    }

    @Test
    public void testDeleteShowtime() throws Exception {
        // Create and save showtime
        Instant startTime = Instant.now().plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS);
        Instant endTime = startTime.plus(120, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.SECONDS);
        ShowtimeRequest showtimeRequest = new ShowtimeRequest(testMovie.getId(), 12.99, testTheater.getName(), startTime, endTime);
        ShowtimeRequest showtimeRequest2 = new ShowtimeRequest(testMovie.getId(), 12.99, testTheater.getName(),
                startTime.plus(1, ChronoUnit.DAYS), endTime.plus(1, ChronoUnit.DAYS));

        // Add showtime
        MvcResult addResult = mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtimeRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Extract showtime ID
        Showtime savedShowtime = objectMapper.readValue(
                addResult.getResponse().getContentAsString(), Showtime.class);

        // add second showtime
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtimeRequest2)))
                .andExpect(status().isOk())
                .andReturn();

        // Verify showtime was added
        assertThat(showtimeRepository.findAll()).hasSize(2);

        // Delete showtime
        mockMvc.perform(delete("/showtimes/{showtimeId}", savedShowtime.getId()))
                .andExpect(status().isOk());

        // Verify showtime was deleted
        assertThat(showtimeRepository.findAll()).hasSize(1);
        // Verify second showtime is still in database
        assertThat(showtimeRepository.findAll().getFirst().getId()).isNotEqualTo(savedShowtime.getId());
    }

    @Test
    public void testDeleteShowtimeNotFound() throws Exception {
        // Attempt to delete a showtime that does not exist
        mockMvc.perform(delete("/showtimes/{showtimeId}", 100))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsStringIgnoringCase("not found")));
    }

    @Test
    public void testAddShowtimeWithNonExistMovie() throws Exception {
        // Create test data with non-existent movie ID
        Instant startTime = Instant.now().plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS);
        Instant endTime = startTime.plus(148, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.SECONDS);
        ShowtimeRequest showtimeRequest = new ShowtimeRequest(1000L, 12.99, testTheater.getName(), startTime, endTime);

        // Attempt to add showtime with non-existent movie ID
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtimeRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsStringIgnoringCase("not found")));

        // Verify database state - no showtime should be added
        assertThat(showtimeRepository.findAll()).hasSize(0);
    }
}