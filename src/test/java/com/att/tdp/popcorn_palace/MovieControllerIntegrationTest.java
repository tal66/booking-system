package com.att.tdp.popcorn_palace;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
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
//import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MovieControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestDatabaseUtility testDatabaseUtility;

    @BeforeEach
    public void setup() {
        testDatabaseUtility.cleanDatabase();
    }

    @Test
    public void testAddMovieAndGetById() throws Exception {
        // Create and add movie
        Movie movie = new Movie("The Shawshank Redemption", "Drama", 142, 9.3, 1994);

        MvcResult addResult = mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value(movie.getTitle()))
                .andReturn();

        // Extract the saved movie ID
        Movie savedMovie = objectMapper.readValue(
                addResult.getResponse().getContentAsString(), Movie.class);

        // Verify retrieval works
        mockMvc.perform(get("/movies/{movieTitle}", savedMovie.getTitle()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedMovie.getId()));

        // Verify database state
        assertThat(movieRepository.findAll()).hasSize(1);
    }

    @Test
    public void testAddDuplicateMovieTitle() throws Exception {
        // Create and add first movie
        Movie movie = new Movie("Inception", "Sci-Fi", 148, 8.8, 2010);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isOk());

        // Try another movie with same title
        Movie duplicateMovie = new Movie("Inception", "Action", 120, 7.5, 2020);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateMovie)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());

        // Verify only one movie was added to the database
        assertThat(movieRepository.findAll()).hasSize(1);
    }

    @Test
    public void testUpdateMovieSuccess() throws Exception {
        // Create and add initial movie
        Movie movie = new Movie("The Matrix", "Sci-Fi", 136, 8.7, 1999);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isOk());

        // Update the movie with new details
        Movie updatedMovie = new Movie("The Matrix", "Sci-Fi/Action", 136, 9.0, 1999);

        mockMvc.perform(post("/movies/update/{movieTitle}", movie.getTitle())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMovie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movie").value(updatedMovie.getTitle()));

        // Verify the movie was updated in the database
        Movie retrievedMovie = movieRepository.findByTitle(movie.getTitle()).orElseThrow();
        assertThat(retrievedMovie.getGenre()).isEqualTo("Sci-Fi/Action");
        assertThat(retrievedMovie.getRating()).isEqualTo(9.0);
    }

    @Test
    public void testUpdateNonExistentMovie() throws Exception {
        // Attempt to update a movie that doesn't exist
        String nonExistentTitle = "Non-Existent Movie";
        Movie updatedMovie = new Movie("Updated Title", "Drama", 120, 8.0, 2020);

        mockMvc.perform(post("/movies/update/{movieTitle}", nonExistentTitle)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMovie)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Movie not found"))
                .andExpect(jsonPath("$.movie").value(nonExistentTitle));

        // Verify the database remains unchanged
        assertThat(movieRepository.findAll()).isEmpty();
    }

    @Test
    public void deleteMovieSuccess() throws Exception {
        // Create and add movie
        Movie movie = new Movie("The Dark Knight", "Action", 152, 9.0, 2008);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isOk());

        assertThat(movieRepository.findAll()).hasSize(1);

        // Delete movie
        mockMvc.perform(delete("/movies/{movieTitle}", movie.getTitle()))
                .andExpect(status().isOk());

        // Verify movie was deleted from database
        assertThat(movieRepository.findAll()).isEmpty();
    }
}