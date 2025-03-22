-- Insert sample movies
INSERT INTO movies (title, genre, duration, rating, release_year)
VALUES
  ('The Shawshank Redemption', 'Drama', 142, 9.3, 1994),
  ('Iron Man', 'Action, Sci-Fi', 126, 7.9, 2008),
  ('The Incredible Hulk', 'Action, Sci-Fi', 112, 6.6, 2008),
  ('Iron Man 2', 'Action, Sci-Fi', 124, 7.0, 2010),
  ('Thor', 'Action, Fantasy', 115, 7.0, 2011),
  ('Captain America: The First Avenger', 'Action, Sci-Fi', 124, 6.9, 2011),
  ('The Avengers', 'Action, Sci-Fi', 143, 8.0, 2012),
  ('Iron Man 3', 'Action, Sci-Fi', 130, 7.1, 2013),
  ('Thor: The Dark World', 'Action, Fantasy', 112, 6.8, 2013),
  ('Captain America: The Winter Soldier', 'Action, Sci-Fi', 136, 7.8, 2014),
  ('Guardians of the Galaxy', 'Action, Sci-Fi', 121, 8.0, 2014),
  ('Avengers: Age of Ultron', 'Action, Sci-Fi', 141, 7.3, 2015),
  ('Ant-Man', 'Action, Sci-Fi', 117, 7.3, 2015),
  ('Captain America: Civil War', 'Action, Sci-Fi', 147, 7.8, 2016),
  ('Doctor Strange', 'Action, Fantasy', 115, 7.5, 2016),
  ('Guardians of the Galaxy Vol. 2', 'Action, Sci-Fi', 136, 7.6, 2017),
  ('Spider-Man: Homecoming', 'Action, Sci-Fi', 133, 7.4, 2017),
  ('Thor: Ragnarok', 'Action, Comedy', 130, 7.9, 2017),
  ('Black Panther', 'Action, Sci-Fi', 134, 7.3, 2018),
  ('Avengers: Infinity War', 'Action, Sci-Fi', 149, 8.4, 2018),
  ('Ant-Man and the Wasp', 'Action, Sci-Fi', 118, 7.0, 2018),
  ('Captain Marvel', 'Action, Sci-Fi', 123, 6.8, 2019),
  ('Avengers: Endgame', 'Action, Sci-Fi', 181, 8.4, 2019),
  ('Spider-Man: Far From Home', 'Action, Sci-Fi', 129, 7.4, 2019),
  ('Black Widow', 'Action, Sci-Fi', 134, 6.7, 2021),
  ('Shang-Chi and the Legend of the Ten Rings', 'Action, Fantasy', 132, 7.4, 2021),
  ('Eternals', 'Action, Fantasy', 156, 6.3, 2021),
  ('Spider-Man: No Way Home', 'Action, Sci-Fi', 148, 8.2, 2021),
  ('Doctor Strange in the Multiverse of Madness', 'Action, Fantasy', 126, 6.9, 2022),
  ('Thor: Love and Thunder', 'Action, Comedy', 119, 6.3, 2022),
  ('Black Panther: Wakanda Forever', 'Action, Sci-Fi', 161, 7.3, 2022),
  ('Ant-Man and the Wasp: Quantumania', 'Action, Sci-Fi', 124, 6.1, 2023),
  ('Guardians of the Galaxy Vol. 3', 'Action, Sci-Fi', 150, 7.8, 2023),
  ('The Marvels', 'Action, Sci-Fi', 105, 5.6, 2023),
  ('X-Men', 'Action, Sci-Fi', 104, 7.4, 2000),
  ('X2', 'Action, Sci-Fi', 134, 7.4, 2003),
  ('X-Men: The Last Stand', 'Action, Sci-Fi', 104, 6.6, 2006),
  ('X-Men Origins: Wolverine', 'Action, Sci-Fi', 107, 6.6, 2009),
  ('X-Men: First Class', 'Action, Sci-Fi', 132, 7.7, 2011),
  ('The Wolverine', 'Action, Sci-Fi', 126, 6.7, 2013),
  ('X-Men: Days of Future Past', 'Action, Sci-Fi', 132, 8.0, 2014),
  ('Deadpool', 'Action, Comedy', 108, 8.0, 2016),
  ('X-Men: Apocalypse', 'Action, Sci-Fi', 144, 6.9, 2016),
  ('Logan', 'Action, Drama', 137, 8.1, 2017),
  ('Deadpool 2', 'Action, Comedy', 119, 7.7, 2018),
  ('Dark Phoenix', 'Action, Sci-Fi', 113, 5.7, 2019)  ;

INSERT INTO theaters (name, num_seats)
VALUES
    ('Theater 1', 30),
    ('Theater 2', 30),
    ('Theater 3', 50),
    ('Theater 4', 50),
    ('Theater 5', 50),
    ('Theater 6', 50),
    ('Theater 7', 50),
    ('Theater 8', 50),
    ('Sample Theater', 50),
    ('IMAX', 100);


INSERT INTO showtimes (price, start_time, end_time, movie_id, theater_id)
VALUES
    (10.0, '2024-07-01 18:00:00', '2024-07-01 20:00:00', 1, 1),
    (10.0, '2024-07-01 18:00:00', '2024-07-01 20:00:00', 2, 2),
    (10.0, '2024-07-01 18:00:00', '2024-07-01 20:00:00', 3, 3),
    (10.0, '2024-07-01 18:00:00', '2024-07-01 20:00:00', 4, 4),
    (10.0, '2024-07-01 18:00:00', '2024-07-01 20:00:00', 5, 5),
    (10.0, '2024-07-01 18:00:00', '2024-07-01 20:00:00', 6, 6),
    (10.0, '2024-07-01 18:00:00', '2024-07-01 20:00:00', 7, 7) ;


