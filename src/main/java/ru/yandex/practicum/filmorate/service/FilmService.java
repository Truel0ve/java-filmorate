package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.database.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.validators.FilmValidator;
import ru.yandex.practicum.filmorate.validators.IdValidator;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FilmService implements FilmStorage {
    private final FilmDbStorage filmStorage;
    private final FilmValidator filmValidator;
    private final IdValidator idValidator;

    // Создать новый фильм
    @Override
    public Film createFilm(Film film) {
        filmValidator.validate(film);
        return filmStorage.createFilm(film);
    }

    // Обновить данные фильма
    @Override
    public Film updateFilm(Film film) {
        idValidator.validateFilmId(film.getId());
        filmValidator.validate(film);
        return filmStorage.updateFilm(film);
    }

    // Удалить фильм
    @Override
    public void deleteFilm(Long filmId) {
        idValidator.validateFilmId(filmId);
        filmStorage.deleteFilm(filmId);
    }

    // Получить данные фильма по ID
    @Override
    public Film getFilmById(Long filmId) {
        idValidator.validateFilmId(filmId);
        return filmStorage.getFilmById(filmId);
    }

    // Получить список всех фильмов
    @Override
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    // Получить отсортированный по количеству лайков список фильмов, с опциональной возможностью фильтрации по году и жанру
    public Set<Film> getPopularFilms(Long year, Long genreId) {
        Set<Film> sortedByLikes = new TreeSet<>(getAllFilms());
        if (year != null && genreId != null) {                                      // оба фильтра
            return filterByGenre(filterByYear(sortedByLikes, year), genreId);
        } else if (year != null) {                                                  // по году
            return filterByYear(sortedByLikes, year);
        } else if (genreId != null) {                                               // по жанру
            return filterByGenre(sortedByLikes, genreId);
        } else return sortedByLikes;                                                //без фильтра
    }

    // Фильтрация по жанру
    private Set<Film> filterByGenre(Set<Film> films, long genreId) {
        return films
                .stream()
                .filter(f -> f.getGenres()
                        .stream()
                        .anyMatch(g -> g.getId().equals(genreId)))
                .collect(Collectors.toSet());
    }

    // Фильтрация по году
    private Set<Film> filterByYear(Set<Film> films, long year) {
        return films
                .stream()
                .filter(f -> f.getReleaseDate().getYear() == year)
                .collect(Collectors.toSet());
    }

    // Получить список фильмов общих с другом
    public Set<Film> getCommonFilmsByFriends(Long userId, Long friendId) {
        return new TreeSet<>(filmStorage.getCommonFilmsByFriends(userId, friendId));
    }

    // Получить список рекомендованных фильмов для пользователя
    public List<Film> getRecommendations(Long userId) {
        return filmStorage.getRecommendations(userId);
    }

    // Поиск фильмов по подстроке с опциональными параметрами поиска по названию и режиссёру
    public Set<Film> searchFilm(String query, String director, String title) {
        Set<Film> sortedFilms = new TreeSet<>(getAllFilms());
        Set<Film> result = new TreeSet<>();
        if (director != null && title != null) {
            result.addAll(searchFilmByName(sortedFilms, query));
            result.addAll(searchFilmByDirector(sortedFilms, query));
            return result;
        } else if (director != null) {
            result.addAll(searchFilmByDirector(sortedFilms, query));
            return result;
        } else if (title != null) {
            result.addAll(searchFilmByName(sortedFilms, query));
            return result;
        } else return sortedFilms;
    }

    // Поиск фильмов по названию
    private Set<Film> searchFilmByName(Set<Film> films, String query) {
        return films
                .stream()
                .filter(f -> f.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toSet());
    }

    // Поиск фильмов по режиссёру
    private Set<Film> searchFilmByDirector(Set<Film> films, String query) {
        return films
                .stream()
                .filter(f -> f.getDirectors()
                        .stream()
                        .anyMatch(d -> d.getName().toLowerCase().contains(query.toLowerCase())))
                .collect(Collectors.toSet());
    }
}