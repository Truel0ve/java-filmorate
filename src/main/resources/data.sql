DELETE FROM films;
ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1;

DELETE FROM users;
ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;

DELETE FROM mpa;
ALTER TABLE mpa ALTER COLUMN mpa_id RESTART WITH 1;

DELETE FROM genres;
ALTER TABLE genres ALTER COLUMN genre_id RESTART WITH 1;

DELETE FROM directors;
ALTER TABLE directors ALTER COLUMN director_id RESTART WITH 1;

DELETE FROM reviews;
ALTER TABLE reviews ALTER COLUMN review_id RESTART WITH 1;

DELETE FROM events;
ALTER TABLE events ALTER COLUMN event_id RESTART WITH 1;

DELETE FROM genre_list;

DELETE FROM friend_list;

DELETE FROM like_list;

DELETE FROM director_list;

DELETE FROM review_like_list;

DELETE FROM event_types;

DELETE FROM operations;

INSERT INTO mpa (mpa_name) values('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');

INSERT INTO genres (genre_name) values('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик');

INSERT INTO event_types (event_type) values('LIKE'), ('REVIEW'), ('FRIEND');

INSERT INTO operations (operation) values('REMOVE'), ('ADD'), ('UPDATE');