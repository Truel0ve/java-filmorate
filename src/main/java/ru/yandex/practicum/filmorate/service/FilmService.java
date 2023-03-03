package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.database.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.*;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Getter
public class FilmService implements FilmStorage, LikeStorage, MpaStorage, GenreStorage {
    private final FilmDbStorage filmStorage;
    private final UserService userService;
    private final DirectorService directorService;
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
    public void deleteFilm(Long filmId) {
        validateFilmId(filmId);
        filmStorage.deleteFilm(filmId);
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
    }

    // Удалить лайк фильму от пользователя
    @Override
    public void deleteLike(Long filmId, Long userId) {
        validateFilmId(filmId);
        userService.validateUserId(userId);
        filmStorage.getLikeDbStorage().deleteLike(filmId, userId);
    }

    // Отсортировать список всех фильмов по убыванию от наиболее популярных к наименее популярным по количеству лайков
    public Set<Film> getPopularFilms() {
        return new TreeSet<>(getAllFilms());
    }

    // Получить список всех фильмов режиссёра, отсортированных по годам или количеству лайков
    public List<Film> getDirectorsFilms(Long directorId, String sortBy) {
        List<Film> directorsFilms = directorService.getDirectorsFilms(directorId);
        if (!directorsFilms.isEmpty()) {
            switch (sortBy) {
                case "year":
                    return directorsFilms.stream()
                            .sorted(Comparator.comparing(Film::getReleaseDate))
                            .collect(Collectors.toList());
                case "likes":
                    return directorsFilms.stream()
                            .sorted(Comparator.comparingInt(f -> f.getLikes().size()))
                            .collect(Collectors.toList());
                default:
                    throw new ArgumentNotFoundException("Неверный параметр запроса");
            }
        } else {
            throw new ArgumentNotFoundException("В базе нет фильмов выбранного режиссёра");
        }
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

    // Получить список фильмов общих с другом
    public Set<Film> getCommonFilmsByFriends(Long userId, Long friendId) {
        return new TreeSet<>(filmStorage.getCommonFilmsByFriends(userId, friendId));
    }

    // Проверить корректность передаваемого ID фильма
    private void validateFilmId(Long filmId) {
        if (filmId == null || filmStorage.getFilmById(filmId) == null) {
            throw new NullPointerException("ID фильма не задан или отсутствует в базе.");
        }
    }
}
