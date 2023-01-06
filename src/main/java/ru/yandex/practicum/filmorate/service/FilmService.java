package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.classes.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
@AllArgsConstructor
@Slf4j
@Getter
public class FilmService implements FilmStorage {
    private final InMemoryFilmStorage filmStorage;
    private final UserService userService;
    private final FilmValidator filmValidator;

    @Override
    public Film createFilm(Film film) {
        filmValidator.validate(film);
        return filmStorage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        validateFilmId(film.getId());
        filmValidator.validate(film);
        return filmStorage.updateFilm(film);
    }

    @Override
    public void deleteFilm(Film film) {
        validateFilmId(film.getId());
        filmStorage.deleteFilm(film);
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(filmStorage.getAllFilms().values());
    }

    public Film getFilmById(Long filmId) {
        validateFilmId(filmId);
        return filmStorage.getFilmById(filmId);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        validateFilmId(filmId);
        userService.validateUserId(userId);
        filmStorage.addLike(filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        validateFilmId(filmId);
        userService.validateUserId(userId);
        filmStorage.deleteLike(filmId, userId);
    }

    public Set<Film> getPopularFilms() {
        return new TreeSet<>(filmStorage.getAllFilms().values());
    }

    private void validateFilmId(Long filmId) {
        if (filmId == null || !filmStorage.getAllFilms().containsKey(filmId)) {
            throw new NullPointerException("ID фильма не задан или отсутствует в базе.");
        }
    }
}
