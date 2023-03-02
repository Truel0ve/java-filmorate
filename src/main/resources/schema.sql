CREATE TABLE IF NOT EXISTS mpa (
	mpa_id  	    INTEGER			GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	mpa_name 		VARCHAR(20)		UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
	film_id 		BIGINT 			GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	name 			VARCHAR(20) 	UNIQUE NOT NULL,
	description 	VARCHAR(200) 	NOT NULL,
	release_date 	DATE 			NOT NULL CHECK (release_date >= '1895-12-28'),
	duration 		INTEGER 		NOT NULL CHECK (duration > 0),
	rate			BIGINT			,
	mpa_id 		    INTEGER 		REFERENCES mpa (mpa_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS genres (
	genre_id 	INTEGER 		GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	genre_name 	VARCHAR(20)		UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS genre_list (
	data_id		BIGINT		GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	genre_id 	INTEGER		NOT NULL REFERENCES genres (genre_id) ON DELETE CASCADE,
	film_id 	BIGINT		NOT NULL REFERENCES films (film_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS users (
	user_id 	BIGINT 			GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	email 		VARCHAR(254) 	UNIQUE NOT NULL CHECK (email ~* '^[A-Za-z0-9._+%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$'),
	login 		VARCHAR(20) 	UNIQUE NOT NULL,
	name 		VARCHAR(20) 	NOT NULL,
	birthday 	DATE 			NOT NULL CHECK (birthday != CURRENT_DATE())
);

CREATE TABLE IF NOT EXISTS friend_list (
	data_id		BIGINT		GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	user_id		BIGINT		NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
	friend_id	BIGINT		NOT NULL REFERENCES users (user_id) ON DELETE CASCADE CHECK (friend_id != user_id),
	status		BOOLEAN		NOT NULL
);

CREATE TABLE IF NOT EXISTS like_list (
	data_id  	BIGINT		GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	film_id		BIGINT		NOT NULL REFERENCES films (film_id) ON DELETE CASCADE,
	user_id		BIGINT		NOT NULL REFERENCES users (user_id) ON DELETE CASCADE
);

DELETE FROM mpa;
ALTER TABLE mpa ALTER COLUMN mpa_id RESTART WITH 1;

DELETE FROM films;
ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1;

DELETE FROM genre_list;
--ALTER TABLE genre_list ALTER COLUMN data_id RESTART WITH 1;

DELETE FROM genres;
ALTER TABLE genres ALTER COLUMN genre_id RESTART WITH 1;

DELETE FROM users;
ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;

DELETE FROM friend_list;
--ALTER TABLE friend_list ALTER COLUMN data_id RESTART WITH 1;

DELETE FROM like_list;
--ALTER TABLE like_list ALTER COLUMN data_id RESTART WITH 1;

INSERT INTO mpa (mpa_name) values('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');

INSERT INTO genres (genre_name) values('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик');