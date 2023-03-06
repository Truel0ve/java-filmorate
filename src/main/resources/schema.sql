create table IF NOT EXISTS DIRECTORS
(
    DIRECTOR_ID   BIGINT generated always as identity primary key,
    DIRECTOR_NAME CHARACTER VARYING(50) not null unique
);

create table IF NOT EXISTS EVENT_TYPES
(
    EVENT_TYPE CHARACTER VARYING(20) not null unique
);

create table IF NOT EXISTS GENRES
(
    GENRE_ID   BIGINT generated always as identity primary key,
    GENRE_NAME CHARACTER VARYING(20) not null unique
);

create table IF NOT EXISTS MPA
(
    MPA_ID   BIGINT generated always as identity primary key,
    MPA_NAME CHARACTER VARYING(20) not null unique
);

create table IF NOT EXISTS FILMS
(
    FILM_ID      BIGINT generated always as identity primary key,
    FILM_NAME    CHARACTER VARYING(50)  not null,
    DESCRIPTION  CHARACTER VARYING(200) not null,
    RELEASE_DATE DATE                   not null,
    DURATION     INTEGER                not null,
    RATE         BIGINT,
    MPA_ID       BIGINT references MPA on delete cascade,
    check ("RELEASE_DATE" >= DATE '1895-12-28'),
    check ("DURATION" > 0)
);

create table IF NOT EXISTS DIRECTOR_LIST
(
    DIRECTOR_ID BIGINT references DIRECTORS on delete cascade,
    FILM_ID     BIGINT references FILMS on delete cascade
);

create table IF NOT EXISTS GENRE_LIST
(
    GENRE_ID BIGINT references GENRES on delete cascade,
    FILM_ID  BIGINT references FILMS on delete cascade
);

create table IF NOT EXISTS OPERATIONS
(
    OPERATION CHARACTER VARYING(20) not null unique
);

create table IF NOT EXISTS USERS
(
    USER_ID  BIGINT generated always as identity primary key,
    EMAIL    CHARACTER VARYING(254) not null unique,
    LOGIN    CHARACTER VARYING(20)  not null unique,
    NAME     CHARACTER VARYING(20)  not null,
    BIRTHDAY DATE                   not null,
    check (CAST("EMAIL" AS VARCHAR_IGNORECASE) REGEXP '^[A-Za-z0-9._+%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$'),
    check ("BIRTHDAY" <> CURRENT_DATE)
);

create table IF NOT EXISTS EVENTS
(
    EVENT_ID   BIGINT generated always as identity primary key,
    USER_ID    BIGINT references USERS on delete cascade,
    ENTITY_ID  BIGINT,
    TIMESTAMP  TIMESTAMP not null,
    EVENT_TYPE CHARACTER VARYING(20) references EVENT_TYPES (EVENT_TYPE) on delete cascade,
    OPERATION  CHARACTER VARYING(20) references OPERATIONS (OPERATION) on delete cascade
);

create table IF NOT EXISTS FRIEND_LIST
(
    USER_ID   BIGINT references USERS on delete cascade,
    FRIEND_ID BIGINT references USERS on delete cascade,
    STATUS    BOOLEAN not null,
    check ("FRIEND_ID" <> "USER_ID")
);

create table IF NOT EXISTS LIKE_LIST
(
    FILM_ID BIGINT references FILMS on delete cascade,
    USER_ID BIGINT references USERS on delete cascade
);

create table IF NOT EXISTS REVIEWS
(
    REVIEW_ID   BIGINT generated always as identity primary key,
    USER_ID     BIGINT references USERS on delete cascade,
    FILM_ID     BIGINT references FILMS on delete cascade,
    CONTENT     CHARACTER VARYING(200) not null,
    IS_POSITIVE BOOLEAN
);

create table IF NOT EXISTS REVIEW_LIKE_LIST
(
    REVIEW_ID BIGINT references REVIEWS on delete cascade,
    USER_ID   BIGINT references USERS on delete cascade,
    IS_LIKE   BOOLEAN not null,
    USEFUL    INTEGER default 0
);

