CREATE TABLE IF NOT EXISTS mpa_rating (
	rating_id 	INTEGER			PRIMARY KEY,
	name 		VARCHAR(20)		UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
	film_id 		BIGINT 			GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	name 			VARCHAR(50) 	UNIQUE NOT NULL,
	description 	VARCHAR(200) 	NOT NULL,
	release_date 	DATE 			NOT NULL CHECK (release_date >= '1895-12-28'),
	duration 		INTEGER 		NOT NULL CHECK (duration > 0),
	rate            BIGINT          ,
	rating_id 		INTEGER 		REFERENCES mpa_rating (rating_id) ON DELETE CASCADE
);
DELETE FROM films;
ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1;

CREATE TABLE IF NOT EXISTS genre_list (
	genre_id 	INTEGER		PRIMARY KEY,
	film_id 	BIGINT		NOT NULL REFERENCES films (film_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS genres (
	genre_id 	INTEGER 		PRIMARY KEY REFERENCES genre_list (genre_id) ON DELETE CASCADE,
	name 		VARCHAR(20)		UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
	user_id 	BIGINT 			GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	email 		VARCHAR(254) 	UNIQUE NOT NULL CHECK (email ~* '^[A-Za-z0-9._+%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$'),
	login 		VARCHAR(20) 	UNIQUE NOT NULL,
	name 		VARCHAR(20) 	NOT NULL,
	birthday 	DATE 			NOT NULL CHECK (birthday <= CURRENT_DATE)
);
DELETE FROM users;
ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;

CREATE TABLE IF NOT EXISTS friend_list (
	user_id		BIGINT		PRIMARY KEY REFERENCES users (user_id) ON DELETE CASCADE,
	friend_id	BIGINT		NOT NULL REFERENCES users (user_id) ON DELETE CASCADE CHECK (friend_id != user_id),
	status		BOOLEAN		NOT NULL
);

CREATE TABLE IF NOT EXISTS like_list (
	film_id		BIGINT		PRIMARY KEY REFERENCES films (film_id) ON DELETE CASCADE,
	user_id		BIGINT		NOT NULL REFERENCES users (user_id) ON DELETE CASCADE
);