package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utilities.ValidationException;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final List<Film> films = new ArrayList<>();
    private final FilmHandler filmHandler = new FilmHandler();

    @GetMapping
    public List<Film> getFilms() {
        filmHandler.logRequestMethod(RequestMethod.GET);
        return films;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmHandler.handleRequest(film, RequestMethod.POST);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmHandler.handleRequest(film, RequestMethod.PUT);
    }

    private class FilmHandler {
        private final FilmValidator filmValidator = new FilmValidator();
        private Film handleRequest(Film film, RequestMethod requestMethod) {
            logRequestMethod(requestMethod);
            try {
                filmValidator.validateFilm(film);
                return handleFilm(film, requestMethod);
            } catch (IllegalArgumentException | ValidationException e) {
                log.warn(e.getMessage(), e);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }

        private void logRequestMethod(RequestMethod requestMethod) {
            log.debug("Получен запрос " + requestMethod + "/films.");
        }

        private Film handleFilm(Film newFilm, RequestMethod requestMethod) throws IllegalArgumentException {
            switch (requestMethod) {
                case POST:
                    return postFilm(newFilm);
                case PUT:
                    validateId(newFilm);
                    return putFilm(newFilm);
                default:
                    throw new IllegalArgumentException("Запрашиваемый метод не поддерживается.\n" +
                            "Для добавления или изменения фильма выберите POST- или PUT-запрос.");
            }
        }

        private Film postFilm(Film newFilm) {
            if (!films.isEmpty()) {
                for (Film film : films) {
                    if (film.getName().equals(newFilm.getName())) {
                        log.info("Фильм \"" + film.getName() + "\" уже есть в базе.");
                        throw new ResponseStatusException(HttpStatus.OK);
                    }
                }
                newFilm.setId(films.size() + 1);
            } else {
                newFilm.setId(1);
            }
            films.add(newFilm);
            log.info("Добавлен новый фильм \"" + newFilm.getName() + "\".");
            return newFilm;
        }

        private Film putFilm(Film newFilm) {
            if (!films.isEmpty()) {
                for (int i = 0; i < films.size(); i++) {
                    Film film = films.get(i);
                    if (film.getId().equals(newFilm.getId())) {
                        films.set(i, newFilm);
                        log.info("Внесены изменения в данные фильма с ID=" + newFilm.getId() + "\".");
                        return newFilm;
                    }
                }
            }
            log.info("Фильма с указанным ID=" + newFilm.getId() + " нет в базе.");
            throw new ResponseStatusException(HttpStatus.OK);
        }

        private void validateId(Film newFilm) {
            if (newFilm.getId() == null || newFilm.getId() > films.size()) {
                log.warn("ID фильма не задан или отсутствует в базе.");
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
}