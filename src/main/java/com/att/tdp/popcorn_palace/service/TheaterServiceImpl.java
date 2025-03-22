package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.model.Theater;
import com.att.tdp.popcorn_palace.repository.TheaterRepository;
import com.att.tdp.popcorn_palace.service.interfaces.TheaterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TheaterServiceImpl implements TheaterService {

    private final TheaterRepository theaterRepository;
    private static final Logger logger = LoggerFactory.getLogger(TheaterServiceImpl.class);

    @Autowired
    public TheaterServiceImpl(TheaterRepository theaterRepository) {
        this.theaterRepository = theaterRepository;
    }

    @Override
    public Optional<Theater> findById(Long id) {
        return theaterRepository.findById(id);
    }

    @Override
    public List<Theater> getAllTheaters() {
        return theaterRepository.findAll();
    }

    @Override
    public Optional<Theater> addTheater(Theater theater) {
        try {
            Theater savedTheater = theaterRepository.save(theater);
            return Optional.of(savedTheater);
        } catch (Exception e) {
            logger.error("Error adding theater: {}", e.getMessage());
            return Optional.empty();
        }
    }


    @Override
    public Optional<Theater> findByName(String name) {
        return theaterRepository.findByName(name);
    }
}