package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
    private int newId = 0;
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
            } catch (ValidationException e) {
                log.warn(e.getMessage(), e);
                return null;
            }
        }

        private void logRequestMethod(RequestMethod requestMethod) {
            log.debug("Получен запрос " + requestMethod + "/films.");
        }

        private Film handleFilm(Film newFilm, RequestMethod requestMethod) {
            if (!films.isEmpty()) {
                for (int i = 0; i < films.size(); i++) {
                    Film film = films.get(i);
                    if (film.getName().equals(newFilm.getName())) {
                        switch (requestMethod) {
                            case POST:
                                log.info("Фильм \"" + film.getName() + "\" уже есть в базе.");
                                return film;
                            case PUT:
                                newFilm.setId(++newId);
                                films.set(i, newFilm);
                                log.info("Внесены изменения в данные фильма \"" + newFilm.getName() + "\".");
                                return newFilm;
                        }
                    }
                }
            }
            newFilm.setId(++newId);
            films.add(newFilm);
            log.info("Добавлен новый фильм \"" + newFilm.getName() + "\".");
            return newFilm;
        }
    }
}