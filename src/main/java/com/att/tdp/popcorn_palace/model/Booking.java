package com.att.tdp.popcorn_palace.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bookings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"showtime_id", "seat_number"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "showtime_id", nullable = false, referencedColumnName = "id")
    private Showtime showtime;

    @NotNull(message = "Seat number is required")
    @Min(value = 1, message = "Seat number must be at least 1")
    private Integer seatNumber;

    @NotBlank(message = "User ID is required")
    private String userId;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    // Constructor without id
    public Booking(Showtime showtime, Integer seatNumber, String userId) {
        this.showtime = showtime;
        this.seatNumber = seatNumber;
        this.userId = userId;
    }

    // Constructor without userId
//    public Booking(Showtime showtime, Integer seatNumber) {
//        this.showtime = showtime;
//        this.seatNumber = seatNumber;
//    }
}