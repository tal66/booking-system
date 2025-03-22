-- 'script' must not be null or empty


-- add index to title
--CREATE INDEX idx_movies_title ON movies (title);
CREATE INDEX idx_movie_title_case_insensitive ON movies (UPPER(title));
CREATE INDEX idx_theater_name_case_insensitive ON theaters (UPPER(name));
CREATE INDEX IF NOT EXISTS idx_showtime_timerange ON showtimes (start_time, end_time);
CREATE INDEX IF NOT EXISTS idx_showtime_theater_id ON showtimes (theater_id);

-- Postgres example
--CREATE INDEX idx_movie_title_trgm ON movies USING gin (title gin_trgm_ops);

--CREATE DATABASE popcorn_palace;

-- Drop table if it exists
--DROP TABLE IF EXISTS movies;
--
---- Create movies table
--CREATE TABLE movies (
--  id SERIAL PRIMARY KEY,
--  title VARCHAR(255) NOT NULL,
--  genre VARCHAR(100) NOT NULL,
--  duration INTEGER NOT NULL,
--  rating DECIMAL(3,1) NOT NULL,
--  release_year INTEGER NOT NULL
--);
--
