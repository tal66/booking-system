package com.att.tdp.popcorn_palace.model;

import com.att.tdp.popcorn_palace.validation.ShowtimeValidator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "showtimes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ShowtimeValidator
public class Showtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false, referencedColumnName = "id")
    private Movie movie;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be at least 0.0")
    private Double price;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "theater_id", nullable = false, referencedColumnName = "id")
    private Theater theater;

    @NotNull(message = "Start time is required")
    private Instant startTime;

    @NotNull(message = "End time is required")
    private Instant endTime;

    // ctor without id
    public Showtime(Movie movie, Double price, Theater theater, Instant startTime, Instant endTime) {
        this.movie = movie;
        this.price = price;
        this.theater = theater;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @JsonProperty("movieId")
    public Long getMovieId() {
        return (movie != null) ? movie.getId() : null;
    }

    @JsonProperty("movie")
    public String getMovieTitle() {
        return (movie != null) ? movie.getTitle() : null;
    }

    @JsonProperty("theater") // expose theater name
    public String getTheaterName() {
        return (theater != null) ? theater.getName() : null;
    }
}