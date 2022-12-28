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

    private final Map<Integer, Film> films = new HashMap<>();
    private int newId = 0;

    @Override
    public Map<Integer, Film> getAll() {
        return films;
    }

    @Override
    public Film create(Film film) {
        if (!films.isEmpty()) {
            for (Film someFilm : films.values()) {
                if (someFilm.getName().equals(film.getName())) {
                    log.info("Фильм \"" + film.getName() + "\" уже есть в базе.");
                    throw new ResponseStatusException(HttpStatus.OK);
                }
            }
        }
        film.setId(++newId);
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм \"" + film.getName() + "\".");
        return film;
    }

    @Override
    public Film update(Film film) {
        validateId(film);
        films.replace(film.getId(), film);
        log.info("Внесены изменения в данные фильма \"" + film.getName() + "\".");
        return film;
    }

    @Override
    public void delete(Film film) {
        validateId(film);
        films.remove(film.getId());
        log.info("Фильм \"" + film.getName() + "\" удален из базы.");
    }

    private void validateId(Film film) {
        if (film.getId() == null || !films.containsKey(film.getId())) {
            log.warn("ID фильма не задан или отсутствует в базе.");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
