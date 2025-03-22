
CREATE INDEX idx_movie_title_case_insensitive ON movies (UPPER(title));

CREATE INDEX idx_theater_name_case_insensitive ON theaters (UPPER(name));

CREATE INDEX IF NOT EXISTS idx_showtime_timerange ON showtimes (start_time, end_time);
CREATE INDEX IF NOT EXISTS idx_showtime_theater_id ON showtimes (theater_id);

CREATE INDEX idx_bookings_showtime_id_seat_number ON bookings (showtime_id, seat_number);

-- Postgres example
--CREATE INDEX idx_movie_title_trgm ON movies USING gin (title gin_trgm_ops);

--CREATE DATABASE popcorn_palace;

