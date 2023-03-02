package ru.yandex.practicum.filmorate.storage.database.rowmappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .likes(new HashSet<>())
                .rate(rs.getLong("rate"))
                .mpa(new Mpa(rs.getLong("mpa_id"), rs.getString("mpa_name")))
                .genres(new HashSet<>())
                .directors(new HashSet<>())
                .build();
        if (rs.getString("likes") != null) {
            setLikes(rs, film);
        }
        if (rs.getString("genre_id") != null ) {
            setGenres(rs, film);
        }
        if (rs.getString("director_id") != null ) {
            setDirectors(rs, film);
        }
        return film;
    }

    private void setLikes(ResultSet rs, Film film) throws SQLException {
        film.setLikes(Stream.of(rs.getString("likes").split(","))
                .map(Long::parseLong)
                .collect(Collectors.toSet()));
    }

    private void setGenres(ResultSet rs, Film film) throws SQLException {
        List<Long> genresId = Stream.of(rs.getString("genre_id").split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        List<String> genresName = Stream.of(rs.getString("genre_name").split(","))
                .collect(Collectors.toList());
        Set<Genre> genres = new HashSet<>();
        for (int i = 0; i < genresId.size(); i++) {
            genres.add(new Genre(genresId.get(i),genresName.get(i)));
        }
        film.setGenres(genres);
    }

    private void setDirectors(ResultSet rs, Film film) throws SQLException {
        List<Long> directorId = Stream.of(rs.getString("director_id").split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        List<String> directorName = Stream.of(rs.getString("director_name").split(","))
                .collect(Collectors.toList());
        Set<Director> directors = new HashSet<>();
        for (int i = 0; i < directorId.size(); i++) {
            directors.add(new Director(directorId.get(i),directorName.get(i)));
        }
        film.setDirectors(directors);
    }
}