package ru.yandex.practicum.filmorate.controllers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/films")
@Slf4j
@Getter
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<Film> getAllFilms() {
        logRequestMethod(RequestMethod.GET);
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable("id") Long filmId) {
        logRequestMethod(RequestMethod.GET, "/" + filmId);
        return filmService.getFilmById(filmId);
    }

    @PostMapping
    public Film post(@Valid @RequestBody Film film) {
        logRequestMethod(RequestMethod.POST);
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        logRequestMethod(RequestMethod.PUT);
        return filmService.updateFilm(film);
    }

    @DeleteMapping
    public void delete(@Valid @RequestBody Film film) {
        logRequestMethod(RequestMethod.DELETE);
        filmService.deleteFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        logRequestMethod(RequestMethod.PUT, "/" + filmId + "/like/" + userId);
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        logRequestMethod(RequestMethod.DELETE, "/" + filmId + "/like/" + userId);
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Long count) {
        logRequestMethod(RequestMethod.GET, "/popular?count=" + count);
        return filmService.getPopularFilms().stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    private void logRequestMethod(RequestMethod requestMethod) {
        log.debug("Получен запрос " + requestMethod + " по адресу: /films");
    }

    private void logRequestMethod(RequestMethod requestMethod, String path) {
        log.debug("Получен запрос " + requestMethod + " по адресу: /films" + path);
    }
}