package com.att.tdp.popcorn_palace;

import com.att.tdp.popcorn_palace.repository.BookingRepository;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import com.att.tdp.popcorn_palace.repository.TheaterRepository;
import org.springframework.stereotype.Component;

/**
 * Utility class for test database operations.
 */
@Component
public class TestDatabaseUtility {

    private final BookingRepository bookingRepository;
    private final ShowtimeRepository showtimeRepository;
    private final TheaterRepository theaterRepository;
    private final MovieRepository movieRepository;

    public TestDatabaseUtility(
            BookingRepository bookingRepository,
            ShowtimeRepository showtimeRepository,
            TheaterRepository theaterRepository,
            MovieRepository movieRepository) {
        this.bookingRepository = bookingRepository;
        this.showtimeRepository = showtimeRepository;
        this.theaterRepository = theaterRepository;
        this.movieRepository = movieRepository;
    }

    public void cleanDatabase() {
        bookingRepository.deleteAll();
        showtimeRepository.deleteAll();
        theaterRepository.deleteAll();
        movieRepository.deleteAll();
    }
}