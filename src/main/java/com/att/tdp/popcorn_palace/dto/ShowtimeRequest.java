package com.att.tdp.popcorn_palace.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.Instant;

@Data
public class ShowtimeRequest {
    @NotNull(message = "Movie ID is required")
    private Long movieId;

//    @NotBlank(message = "Movie name is required")
//    @Size(min = 1, max = 255, message = "Movie name must be between 1 and 255 characters")
//    private String movie;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be at least 0.0")
    private Double price;

//    @NotNull(message = "Theater ID is required")
//    private Long theaterId;

    @NotBlank(message = "Theater name is required")
    @Size(min = 1, max = 255, message = "Theater name must be between 1 and 255 characters")
    private String theater;

    @NotNull(message = "Start time is required")
    private Instant startTime;

    @NotNull(message = "End time is required")
    private Instant endTime;
}
