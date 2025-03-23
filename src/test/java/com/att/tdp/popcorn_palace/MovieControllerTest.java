package com.att.tdp.popcorn_palace;

import com.att.tdp.popcorn_palace.controller.MovieController;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.service.interfaces.MovieService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieControllerTest {

    @Mock
    private MovieService movieService;

    @InjectMocks
    private MovieController movieController;

    @Test
    void getAllMovies_ReturnsListOfMovies() {
        // Arrange
        Movie movie1 = new Movie("Movie1", "Drama", 142, 9.3, 1994);
        Movie movie2 = new Movie("Movie2", "Crime", 175, 9.2, 1972);
        List<Movie> expectedMovies = Arrays.asList(movie1, movie2);

        when(movieService.getAllMovies()).thenReturn(expectedMovies);

        // Act
        ResponseEntity<List<Movie>> response = movieController.getAllMovies();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedMovies, response.getBody());
        verify(movieService, times(1)).getAllMovies();
    }

    @Test
    void getMovieByTitle_WhenMovieExists_ReturnsMovie() {
        // Arrange
        String movieTitle = "Movie1";
        Movie expectedMovie = new Movie(movieTitle, "Drama", 142, 9.3, 1994);

        when(movieService.findByTitle(movieTitle)).thenReturn(Optional.of(expectedMovie));

        // Act
        ResponseEntity<Object> response = movieController.getMovieByTitle(movieTitle);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedMovie, response.getBody());
        verify(movieService, times(1)).findByTitle(movieTitle);
    }

    @Test
    void getMovieByTitle_WhenMovieDoesNotExist_ReturnsNotFound() {
        // Arrange
        String movieTitle = "Non-existent Movie";

        when(movieService.findByTitle(movieTitle)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Object> response = movieController.getMovieByTitle(movieTitle);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();

        assert responseBody != null;
        assertTrue(responseBody.containsKey("error"), "Response should contain 'error' key");
        assertTrue(responseBody.get("error").contains("not found"), "Error message should indicate item was not found");
        assertEquals(movieTitle, responseBody.get("movie"));
        verify(movieService, times(1)).findByTitle(movieTitle);
    }

    @Test
    void addMovie_Success() {
        // Arrange
        Movie movieToAdd = new Movie("Inception", "Sci-Fi", 148, 8.8, 2010);
        Movie savedMovie = new Movie("Inception", "Sci-Fi", 148, 8.8, 2010);
        savedMovie.setId(1L); // Simulate database assigning ID
        when(movieService.addMovie(movieToAdd)).thenReturn(Optional.of(savedMovie));

        // Act
        ResponseEntity<Movie> response = movieController.addMovie(movieToAdd);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Should return OK status");
        assertEquals(savedMovie, response.getBody(), "Should return the saved movie with ID");
        verify(movieService, times(1)).addMovie(movieToAdd);
    }

}