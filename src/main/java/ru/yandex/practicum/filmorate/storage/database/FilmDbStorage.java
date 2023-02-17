package ru.yandex.practicum.filmorate.storage.database;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.database.rowmappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
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
            String sqlSelectName =
                    "SELECT name " +
                    "FROM films " +
                    "WHERE name = ?";
            String name = jdbcTemplate.queryForObject(sqlSelectName, String.class, film.getName());
            if (Objects.equals(name, film.getName())) {
                throw new ValidationException("Фильм \"" + film.getName() + "\" уже есть в базе.");
            }
        } catch (EmptyResultDataAccessException e) {
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
                preparedStatement.setInt(5, film.getMpa().getId());
                return preparedStatement;
            }, keyHolder);
            Long filmId = Objects.requireNonNull(keyHolder.getKey()).longValue();
            film.setId(filmId);
            if (film.getGenres() != null) {
                film.setGenres(genreDbStorage.validateGenres(film));
            }
            log.info("Добавлен новый фильм \"" + film.getName() + "\".");
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
            film.setGenres(genreDbStorage.validateGenres(film));
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
        log.info("Фильм \"" + film.getName() + "\" удален из базы.");
    }

    // Получить данные фильма по ID
    public Film getFilmById(Long filmId) throws NullPointerException {
        String sqlSelectFilm =
                "SELECT * " +
                "FROM films " +
                "LEFT JOIN mpa ON films.mpa_id = mpa.mpa_id " +
                "WHERE film_id = ?";
        String sqlSelectGenres =
                "SELECT gl.genre_id, g.genre_name " +
                "FROM genre_list AS gl " +
                "LEFT JOIN genres AS g ON gl.genre_id = g.genre_id " +
                "WHERE gl.film_id = ? " +
                "ORDER BY gl.genre_id ASC";
        String sqlSelectLikes =
                "SELECT user_id " +
                "FROM like_list " +
                "WHERE film_id = ? " +
                "ORDER BY user_id ASC";
        Film film = jdbcTemplate.query(sqlSelectFilm, new FilmRowMapper(), filmId).stream()
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Фильм с ID=" + filmId + " отсутствует в базе"));
        film.setGenres(jdbcTemplate.query(sqlSelectGenres,
                (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("genre_name")), filmId));
        film.setLikes(new HashSet<>(jdbcTemplate.query(sqlSelectLikes,
                (rs, rowNum) -> rs.getLong("user_id"), filmId)));
        return film;
    }

    // Получить список всех фильмов
    public Map<Long, Film> getAllFilms() {
        String sqlSelectAll =
                "SELECT * " +
                "FROM films " +
                "LEFT JOIN mpa ON films.mpa_id = mpa.mpa_id";
        return jdbcTemplate.queryForStream(sqlSelectAll, new FilmRowMapper())
                .map(film -> getFilmById(film.getId()))
                .collect(Collectors.toMap(Film::getId, film -> film));
    }
}