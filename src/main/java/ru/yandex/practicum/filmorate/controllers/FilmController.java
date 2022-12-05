package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utilities.ValidationException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    public final Map<Integer, Film> films = new HashMap<>();
    private int newId = 0;
    private final FilmValidator filmValidator = new FilmValidator();

    @GetMapping
    public Map<Integer, Film> getFilms() {
        checkLogDebug(RequestMethod.GET);
        return films;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        checkLogDebug(RequestMethod.POST);
        try {
            if (validateFilm(film)) {
                log.info("Фильм \"" + film.getName() + "\" уже есть в базе.");
                return films.get(film.getId());
            } else {
                films.put(film.getId(), film);
                log.info("Добавлен новый фильм \"" + film.getName() + "\".");
                return film;
            }
        } catch (ValidationException e) {
            log.warn(e.getMessage(), e);
            return null;
        }
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        checkLogDebug(RequestMethod.PUT);
        try {
            if (validateFilm(film)) {
                films.replace(film.getId(), film);
                log.info("Внесены изменения в данные фильма \"" + film.getName() + "\".");
            } else {
                films.put(film.getId(), film);
                log.info("Добавлен новый фильм \"" + film.getName() + "\".");
            }
            return film;
        } catch (ValidationException e) {
            log.warn(e.getMessage(), e);
            return null;
        }
    }

    private void checkLogDebug(RequestMethod requestMethod) {
        log.debug("Получен запрос " + requestMethod + "/films.");
    }

    public boolean validateFilm(Film film) throws ValidationException {
        return filmValidator.isFilmValid(film);
    }

    private class FilmValidator {
        private boolean isFilmValid(Film film) throws ValidationException {
            validateNotNull(film);
            validateName(film);
            validateDescription(film);
            validateReleaseDate(film);
            validateDuration(film);
            return doesTheFilmExist(film);
        }

        private void validateNotNull(Film film) throws ValidationException {
            if (film == null) {
                throw new ValidationException("Данные фильма не указаны.");
            }
        }

        private void validateName(Film film) throws ValidationException {
            if (film.getName() == null || film.getName().isBlank()) {
                throw new ValidationException("Название фильма не указано.");
            }
        }

        private void validateDescription(Film film) throws ValidationException {
            if (film.getDescription() == null || film.getDescription().isBlank()) {
                throw new ValidationException("Описание фильма не указано.");
            }
            if (film.getDescription().length() > 200) {
                throw new ValidationException("Описание фильма не может превышать 200 символов.");
            }
        }

        private void validateReleaseDate(Film film) throws ValidationException {
            if (film.getReleaseDate() == null) {
                throw new ValidationException("Дата релиза не указана.");
            }
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
            }
        }

        private void validateDuration(Film film) throws ValidationException {
            if (film.getDuration() == null) {
                throw new ValidationException("Продолжительность фильма не указана.");
            }
            if (film.getDuration().isNegative() || film.getDuration().isZero()) {
                throw new ValidationException("Продолжительность фильма не может быть меньше или равна 0.");
            }
        }

        private boolean doesTheFilmExist(Film newFilm) {
            if (!films.isEmpty()) {
                for (Film film : films.values()) {
                    if (film.getName().equals(newFilm.getName())) {
                        newFilm.setId(film.getId());
                        return true;
                    }
                }
            }
            newFilm.setId(++newId);
            return false;
        }
    }
}