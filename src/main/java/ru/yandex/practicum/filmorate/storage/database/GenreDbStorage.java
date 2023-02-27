package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    // Получить жанр фильма по ID
    @Override
    public Genre getGenreById(Long id) {
        String sqlSelectGenre =
                "SELECT * " +
                "FROM genres " +
                "WHERE genre_id = ?";
        List<Genre> genres = jdbcTemplate.query(sqlSelectGenre,
                (rs, rowNum) -> new Genre(rs.getLong("genre_id"), rs.getString("genre_name")), id);
        return genres.stream()
                .findFirst()
                .orElseThrow(() -> new ArgumentNotFoundException("Жанр с ID=" + id + " отсутствует в базе"));
    }

    // Получить все доступные жанры фильмов
    @Override
    public List<Genre> getAllGenres() {
        String sqlSelectAll =
                "SELECT * " +
                "FROM genres " +
                "ORDER BY genre_id ASC";
        return jdbcTemplate.query(sqlSelectAll,
                (rs, rowNum) -> new Genre(rs.getLong("genre_id"), rs.getString("genre_name")));
    }

    // Сохранить переданные жанры фильма
    public Set<Genre> setGenres(Film film) {
        return updateGenres(validateGenres(film), film);
    }

    // Проверить и отсортировать жанры передаваемого фильма
    private Set<Genre> validateGenres(Film film) {
        return film.getGenres().stream()
                .map(Genre::getId)
                .distinct()
                .filter(id -> id > 0 && id <= Genre.GenreType.values().length)
                .sorted()
                .map(this::getGenreById)
                .collect(Collectors.toSet());
    }

    // Сохранить пакет ID жанров и фильмов в базу данных
    private Set<Genre> updateGenres(Set<Genre> genres, Film film) {
        List<Object[]> batch = new ArrayList<>();
        for (Genre genre : genres) {
            Object[] values = new Object[] {
                    genre.getId(),
                    film.getId()};
            batch.add(values);
        }
        String sqlInsertGenres =
                "INSERT INTO genre_list (genre_id, film_id) " +
                "VALUES (?,?)";
        jdbcTemplate.batchUpdate(sqlInsertGenres, batch);
        return genres;
    }
}