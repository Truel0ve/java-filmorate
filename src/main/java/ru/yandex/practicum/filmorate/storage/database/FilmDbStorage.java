package ru.yandex.practicum.filmorate.storage.database;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.database.rowmappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Primary
@RequiredArgsConstructor
@Slf4j
@Getter
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final LikeDbStorage likeDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;

    // Создать новый фильм
    @Override
    public Film createFilm(Film film) {
        try {
            String sqlInsertFilm =
                    "INSERT INTO films (name, description, release_date, duration, mpa_id)" +
                    "VALUES (?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement preparedStatement = con.prepareStatement(sqlInsertFilm, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, film.getName());
                preparedStatement.setString(2, film.getDescription());
                preparedStatement.setObject(3, film.getReleaseDate());
                preparedStatement.setInt(4, film.getDuration());
                preparedStatement.setLong(5, film.getMpa().getId());
                return preparedStatement;
            }, keyHolder);
            Long filmId = Objects.requireNonNull(keyHolder.getKey()).longValue();
            film.setId(filmId);
            if (film.getGenres() != null) {
                film.setGenres(genreDbStorage.setGenres(film));
            }
            log.info("Добавлен новый фильм \"" + film.getName() + "\".");
        } catch (DuplicateKeyException e) {
            throw new ValidationException("Фильм \"" + film.getName() + "\" уже есть в базе.");
        }
        return film;
    }

    // Обновить данные фильма
    @Override
    public Film updateFilm(Film film) {
        String sqlUpdateFilm =
                "UPDATE films " +
                "SET name = ?2, description = ?3, release_date = ?4, duration = ?5, mpa_id = ?6 " +
                "WHERE film_id = ?1";
        jdbcTemplate.update(sqlUpdateFilm, film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId());
        if (film.getGenres() != null) {
            String sqlDeleteGenres =
                    "DELETE FROM genre_list " +
                    "WHERE film_id = ?";
            jdbcTemplate.update(sqlDeleteGenres, film.getId());
            film.setGenres(genreDbStorage.setGenres(film));
        }
        log.info("Внесены изменения в данные фильма \"" + film.getName() + "\".");
        return film;
    }

    // Удалить фильм
    @Override
    public void deleteFilm(Film film) {
        String sqlDeleteFilm =
                "DELETE FROM films " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sqlDeleteFilm, film.getId());
        String sqlDeleteGenres =
                "DELETE FROM genre_list " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sqlDeleteGenres, film.getId());
        log.info("Фильм \"" + film.getName() + "\" удален из базы.");
    }

    // Получить данные фильма по ID
    @Override
    public Film getFilmById(Long filmId) {
        String sqlSelectFilm =
                "SELECT f.*, m.mpa_name, " +
                "GROUP_CONCAT (DISTINCT g.genre_id ORDER BY g.genre_id SEPARATOR ',') AS genre_id, " +
                "GROUP_CONCAT (DISTINCT g.genre_name ORDER BY g.genre_id SEPARATOR ',') AS genre_name, " +
                "GROUP_CONCAT (DISTINCT ll.user_id ORDER BY ll.user_id SEPARATOR ',') AS likes " +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                "LEFT JOIN genre_list AS gl ON gl.film_id = f.film_id " +
                "LEFT JOIN genres AS g ON g.genre_id = gl.genre_id " +
                "LEFT JOIN like_list AS ll ON ll.film_id = f.film_id " +
                "WHERE f.film_id = ?" +
                "GROUP BY f.film_id";
        List<Film> filmList = jdbcTemplate.query(sqlSelectFilm, new FilmRowMapper(), filmId);
        return filmList.stream()
                .findFirst()
                .orElseThrow(() -> new ArgumentNotFoundException("Фильм с ID=" + filmId + " отсутствует в базе"));
    }

    // Получить список всех фильмов
    @Override
    public List<Film> getAllFilms() {
        String sqlSelectAllFilms =
                "SELECT f.*, m.mpa_name, " +
                "GROUP_CONCAT (DISTINCT g.genre_id ORDER BY g.genre_id SEPARATOR ',') AS genre_id, " +
                "GROUP_CONCAT (DISTINCT g.genre_name ORDER BY g.genre_id SEPARATOR ',') AS genre_name, " +
                "GROUP_CONCAT (DISTINCT ll.user_id ORDER BY ll.user_id SEPARATOR ',') AS likes " +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                "LEFT JOIN genre_list AS gl ON gl.film_id = f.film_id " +
                "LEFT JOIN genres AS g ON g.genre_id = gl.genre_id " +
                "LEFT JOIN like_list AS ll ON ll.film_id = f.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY f.film_id";
        return jdbcTemplate.queryForStream(sqlSelectAllFilms, new FilmRowMapper()).collect(Collectors.toList());
    }

    public List<Film> getCommonFilmsByFriends(Long userId, Long friendId) {
        String sqlCommonFilms = "SELECT f.*, m.mpa_name, " +
                "GROUP_CONCAT (DISTINCT g.genre_id ORDER BY g.genre_id SEPARATOR ',') AS genre_id, " +
                "GROUP_CONCAT (DISTINCT g.genre_name ORDER BY g.genre_id SEPARATOR ',') AS genre_name, " +
                "GROUP_CONCAT (DISTINCT ll.user_id ORDER BY ll.user_id SEPARATOR ',') AS likes  " +
                "FROM films AS f  " +
                "LEFT JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                "LEFT JOIN genre_list AS gl ON gl.film_id = f.film_id " +
                "LEFT JOIN genres AS g ON g.genre_id = gl.genre_id " +
                "LEFT JOIN like_list AS ll ON ll.film_id = f.film_id " +
                "WHERE f.film_id IN (SELECT FILM_ID " +
                "FROM LIKE_LIST ll  " +
                "WHERE (USER_ID = ?) OR (USER_ID = ?) " +
                "GROUP BY FILM_ID " +
                "HAVING COUNT(USER_ID) > 1) " +
                "GROUP BY f.film_id";

        return jdbcTemplate.query(sqlCommonFilms,
                new FilmRowMapper(), userId, friendId);
    }
}