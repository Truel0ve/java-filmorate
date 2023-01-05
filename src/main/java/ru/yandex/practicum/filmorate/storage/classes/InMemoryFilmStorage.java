package ru.yandex.practicum.filmorate.storage.classes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private Long newId = 0L;

    @Override
    public Film createFilm(Film film) {
        if (!films.isEmpty() && films.values()
                .stream()
                .anyMatch(someFilm -> someFilm.getName().equals(film.getName()))) {
            log.info("Фильм \"" + film.getName() + "\" уже есть в базе.");
            throw new ResponseStatusException(HttpStatus.OK);
        }
        film.setId(++newId);
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм \"" + film.getName() + "\".");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        Long filmId = film.getId();
        films.replace(filmId, film);
        log.info("Внесены изменения в данные фильма \"" + film.getName() + "\".");
        return film;
    }

    @Override
    public void deleteFilm(Film film) {
        Long filmId = film.getId();
        films.remove(filmId);
        log.info("Фильм \"" + film.getName() + "\" удален из базы.");
    }

    public Map<Long, Film> getAllFilms() {
        return films;
    }

    public Film getFilmById(Long filmId) {
        return films.get(filmId);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        films.get(filmId).getLikes().add(userId);
        log.info("Пользователь с ID=" + userId + " поставил лайк фильму \"" + films.get(filmId).getName() + "\".");
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        films.get(filmId).getLikes().remove(userId);
        log.info("Пользователь с ID=" + userId + " убрал лайк фильму \"" + films.get(filmId).getName() + "\".");
    }
}