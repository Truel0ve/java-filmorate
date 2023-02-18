package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    // Получить жанр фильма по ID
    public Genre getGenreById(int id) {
        String sqlSelectGenre =
                "SELECT * " +
                "FROM genres " +
                "WHERE genre_id = ?";
        List<Genre> genres = jdbcTemplate.query(sqlSelectGenre,
                (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("genre_name")), id);
        return genres.stream()
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Жанр с ID=" + id + " отсутствует в базе"));
    }

    // Получить все доступные жанры фильмов
    public List<Genre> getAllGenres() {
        String sqlSelectAll =
                "SELECT * " +
                "FROM genres " +
                "ORDER BY genre_id ASC";
        return jdbcTemplate.query(sqlSelectAll,
                (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("genre_name")));
    }

    // Проверить и отсортировать жанры передаваемого фильма
    public List<Genre> validateGenres(Film film) {
        List<Genre> validatedGenres = film.getGenres().stream()
                .map(Genre::getId)
                .distinct()
                .filter(id -> id > 0 && id <= Genre.GenreType.values().length)
                .map(this::getGenreById)
                .collect(Collectors.toList());
        for (Genre genre : validatedGenres) {
            String sqlInsertGenres =
                    "INSERT INTO genre_list (genre_id, film_id) " +
                    "VALUES (?,?)";
            jdbcTemplate.update(sqlInsertGenres, genre.getId(), film.getId());
        }
        return validatedGenres;
    }
}