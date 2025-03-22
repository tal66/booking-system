package com.att.tdp.popcorn_palace.service.interfaces;

import com.att.tdp.popcorn_palace.model.Theater;

import java.util.List;
import java.util.Optional;

public interface TheaterService {

    Optional<Theater> findById(Long id);

    List<Theater> getAllTheaters();

    Optional<Theater> addTheater(Theater theater);

    Optional<Theater> findByName(String name);
}