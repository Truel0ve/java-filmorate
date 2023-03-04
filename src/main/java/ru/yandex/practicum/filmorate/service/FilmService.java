package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.database.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.database.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.LikeStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
@AllArgsConstructor
@Slf4j
@Getter
public class FilmService implements FilmStorage, LikeStorage, MpaStorage, GenreStorage {
    private final FilmDbStorage filmStorage;
    private final UserService userService;
    private final FilmValidator filmValidator;

    // Создать новый фильм
    @Override
    public Film createFilm(Film film) {
        filmValidator.validate(film);
        return filmStorage.createFilm(film);
    }

    // Обновить данные фильма
    @Override
    public Film updateFilm(Film film) {
        validateFilmId(film.getId());
        filmValidator.validate(film);
        return filmStorage.updateFilm(film);
    }

    // Удалить фильм
    @Override
    public void deleteFilm(Film film) {
        validateFilmId(film.getId());
        filmStorage.deleteFilm(film);
    }

    // Получить данные фильма по ID
    @Override
    public Film getFilmById(Long filmId) {
        validateFilmId(filmId);
        return filmStorage.getFilmById(filmId);
    }

    // Получить список всех фильмов
    @Override
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    // Поставить лайк фильму от пользователя
    @Override
    public void addLike(Long filmId, Long userId) {
        validateFilmId(filmId);
        userService.validateUserId(userId);
        filmStorage.getLikeDbStorage().addLike(filmId, userId);
        userService.getUserStorage().getEventDbStorage().addEvent(userId, filmId, "LIKE", "ADD");
    }

    // Удалить лайк фильму от пользователя
    @Override
    public void deleteLike(Long filmId, Long userId) {
        validateFilmId(filmId);
        userService.validateUserId(userId);
        filmStorage.getLikeDbStorage().deleteLike(filmId, userId);
        userService.getUserStorage().getEventDbStorage().addEvent(userId, filmId, "LIKE", "REMOVE");
    }

    // Отсортировать список всех фильмов по убыванию от наиболее популярных к наименее популярным по количеству лайков
    public Set<Film> getPopularFilms() {
        return new TreeSet<>(getAllFilms());
    }

    // Получить MPA-рейтинг фильма по ID
    @Override
    public Mpa getMpaById(Long id) {
        return filmStorage.getMpaDbStorage().getMpaById(id);
    }

    // Получить список всех доступных MPA-рейтингов фильмов
    @Override
    public List<Mpa> getAllMpa() {
        return filmStorage.getMpaDbStorage().getAllMpa();
    }

    // Получить жанр фильма по ID
    @Override
    public Genre getGenreById(Long id) {
        return filmStorage.getGenreDbStorage().getGenreById(id);
    }

    // Получить все доступные жанры фильмов
    @Override
    public List<Genre> getAllGenres() {
        return filmStorage.getGenreDbStorage().getAllGenres();
    }

    // Проверить корректность передаваемого ID фильма
    private void validateFilmId(Long filmId) {
        if (filmId == null || filmStorage.getFilmById(filmId) == null) {
            throw new NullPointerException("ID фильма не задан или отсутствует в базе.");
        }
    }
}
