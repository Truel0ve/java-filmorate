package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.classes.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
    private final FilmValidator filmValidator = new FilmValidator();

    @GetMapping
    public Map<Integer, Film> get() {
        logRequestMethod(RequestMethod.GET);
        return filmStorage.getAll();
    }

    @PostMapping
    public Film post(@Valid @RequestBody Film film) {
        logRequestMethod(RequestMethod.POST);
        try {
            filmValidator.validate(film);
            return filmStorage.create(film);
        } catch (IllegalArgumentException | ValidationException e) {
            log.warn(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        logRequestMethod(RequestMethod.PUT);
        try {
            filmValidator.validate(film);
            return filmStorage.update(film);
        } catch (IllegalArgumentException | ValidationException e) {
            log.warn(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping
    public void delete(@Valid @RequestBody Film film) {
        logRequestMethod(RequestMethod.DELETE);
        try {
            filmValidator.validate(film);
            filmStorage.delete(film);
        } catch (IllegalArgumentException | ValidationException e) {
            log.warn(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private void logRequestMethod(RequestMethod requestMethod) {
        log.debug("Получен запрос " + requestMethod + "/films.");
    }
}