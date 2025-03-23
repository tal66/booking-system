package com.att.tdp.popcorn_palace.model;

import com.att.tdp.popcorn_palace.validation.MovieValidator;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "movies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@MovieValidator
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    @Column(unique = true)
    private String title;

    @NotBlank(message = "Genre is required")
    @Size(min = 1, max = 100, message = "Genre must be between 1 and 100 characters")
    private String genre;

    @NotNull(message = "Duration (in minutes) is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 12*60, message = "Duration must be at most 12 hours")
    private Integer duration;

//    @NotNull(message = "Rating is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Rating must be at least 0.0")
    @DecimalMax(value = "10.0", inclusive = true, message = "Rating must be at most 10.0")
    private Double rating;

    @NotNull(message = "Release year is required")
    // min,max in validator
    private Integer releaseYear;

    // Constructor without id
    public Movie(String title, String genre, Integer duration, Double rating, Integer releaseYear) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.releaseYear = releaseYear;
    }
}