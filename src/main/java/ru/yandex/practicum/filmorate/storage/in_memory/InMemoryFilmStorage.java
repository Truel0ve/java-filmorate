package ru.yandex.practicum.filmorate.storage.in_memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmLikeStorage;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage, FilmLikeStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private Long newId = 0L;

    @Override
    public Film createFilm(Film film) {
        if (!films.isEmpty() && films.values()
                .stream()
                .anyMatch(someFilm -> someFilm.getName().equals(film.getName()))) {
            throw new ValidationException("Фильм \"" + film.getName() + "\" уже есть в базе.");
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
    public void deleteFilm(Long filmId) {
        String filmName = getFilmById(filmId).getName();
        films.remove(filmId);
        log.info("Фильм \"" + filmName + "\" удален из базы.");
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
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