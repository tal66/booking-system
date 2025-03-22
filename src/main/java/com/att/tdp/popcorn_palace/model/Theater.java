package com.att.tdp.popcorn_palace.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "theaters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Theater {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Theater name is required")
    @Size(min = 1, max = 255, message = "Theater name must be between 1 and 255 characters")
    @Column(unique = true)
    private String name;

    @NotNull(message = "Number of seats is required")
    @Min(value = 1, message = "Theater must have at least 1 seat")
    @Max(value = 1000, message = "Theater cannot have more than 1000 seats")
    private Integer numSeats;

    // ctor without id
    public Theater(String name, Integer numSeats) {
        this.name = name;
        this.numSeats = numSeats;
    }
}