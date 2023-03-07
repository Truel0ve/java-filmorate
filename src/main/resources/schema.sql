
CREATE TABLE IF NOT EXISTS mpa (
	mpa_id 		BIGINT			GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	mpa_name 	VARCHAR(20)		UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
	film_id 		BIGINT 			GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	film_name 		VARCHAR(50) 	NOT NULL,
	description 	VARCHAR(200) 	NOT NULL,
	release_date 	DATE 			NOT NULL CHECK (release_date >= '1895-12-28'),
	duration 		INTEGER 		NOT NULL CHECK (duration > 0),
	rate			BIGINT			,
	mpa_id 			BIGINT 			REFERENCES mpa (mpa_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS genres (
	genre_id 	BIGINT			GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	genre_name 	VARCHAR(20)		UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS genre_list (
	genre_id 	BIGINT		REFERENCES genres (genre_id) ON DELETE CASCADE,
	film_id 	BIGINT		REFERENCES films (film_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS directors (
	director_id 	BIGINT 			GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	director_name 	VARCHAR(50)		UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS director_list (
	director_id 	BIGINT		REFERENCES directors (director_id) ON DELETE CASCADE,
	film_id 		BIGINT		REFERENCES films (film_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS users (
	user_id 	BIGINT 			GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	email 		VARCHAR(254) 	UNIQUE NOT NULL CHECK (email ~* '^[A-Za-z0-9._+%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$'),
	login 		VARCHAR(20) 	UNIQUE NOT NULL,
	name 		VARCHAR(20) 	NOT NULL,
	birthday 	DATE 			NOT NULL CHECK (birthday != CURRENT_DATE())
);

CREATE TABLE IF NOT EXISTS friend_list (
	user_id		BIGINT		REFERENCES users (user_id) ON DELETE CASCADE,
	friend_id	BIGINT		REFERENCES users (user_id) ON DELETE CASCADE CHECK (friend_id != user_id),
	status		BOOLEAN		NOT NULL
);

CREATE TABLE IF NOT EXISTS like_list (
	film_id		BIGINT		REFERENCES films (film_id) ON DELETE CASCADE,
	user_id		BIGINT		REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews (
	review_id 		BIGINT 			GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	user_id			BIGINT			REFERENCES users (user_id) ON DELETE CASCADE,
	film_id			BIGINT			REFERENCES films (film_id) ON DELETE CASCADE,
	content			VARCHAR(200) 	NOT NULL,
	is_positive     BOOLEAN         NOT NULL,
	rating			BIGINT			DEFAULT 0


);

CREATE TABLE IF NOT EXISTS review_like_list (
	review_id 		BIGINT			REFERENCES reviews (review_id) ON DELETE CASCADE,
	user_id			BIGINT			REFERENCES users (user_id) ON DELETE CASCADE,
	is_like			BOOLEAN			NOT NULL
);


CREATE TABLE IF NOT EXISTS event_types (
	event_type 	VARCHAR(20) 	UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS operations (
	operation 	VARCHAR(20) 	UNIQUE NOT NULL
);


CREATE TABLE IF NOT EXISTS events (
	event_id 		BIGINT 			GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	user_id			BIGINT			REFERENCES users (user_id) ON DELETE CASCADE,
	entity_id		BIGINT			,
	timestamp		TIMESTAMP 		NOT NULL,
	event_type		VARCHAR(20)		REFERENCES event_types (event_type) ON DELETE CASCADE,
	operation		VARCHAR(20)		REFERENCES operations (operation) ON DELETE CASCADE
);