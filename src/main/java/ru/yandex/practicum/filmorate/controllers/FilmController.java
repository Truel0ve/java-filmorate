package ru.yandex.practicum.filmorate.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmLikeService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.utility.DefaultLogger;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    private final FilmLikeService filmLikeService;
    private final DirectorService directorService;
    private final DefaultLogger defaultLogger;

    @GetMapping
    public List<Film> getAllFilms() {
        defaultLogger.logRequestMethod(RequestMethod.GET, "/films");
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable("id") Long filmId) {
        defaultLogger.logRequestMethod(RequestMethod.GET, "/films/" + filmId);
        return filmService.getFilmById(filmId);
    }

    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {
        defaultLogger.logRequestMethod(RequestMethod.POST, "/films");
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film putFilm(@Valid @RequestBody Film film) {
        defaultLogger.logRequestMethod(RequestMethod.PUT, "/films");
        return filmService.updateFilm(film);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable("id") Long filmId) {
        defaultLogger.logRequestMethod(RequestMethod.DELETE, "/films/" + filmId);
        filmService.deleteFilm(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        defaultLogger.logRequestMethod(RequestMethod.PUT, "/films/" + filmId + "/like/" + userId);
        filmLikeService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        defaultLogger.logRequestMethod(RequestMethod.DELETE, "/films/" + filmId + "/like/" + userId);
        filmLikeService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10", required = false) Long count,
                                      @RequestParam(required = false) Long genreId,
                                      @RequestParam(required = false) Long year) {
        defaultLogger.logRequestMethod(RequestMethod.GET, "/films/popular?count=" + count);
        return filmService.getPopularFilms(year, genreId).stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getAllDirectorFilms(@PathVariable Long directorId,
                                          @RequestParam(required = false) String sortBy) {
        if (sortBy == null) {
            defaultLogger.logRequestMethod(RequestMethod.GET, "/films/director/" + directorId);
            return directorService.getDirectorsFilms(directorId);
        } else {
            defaultLogger.logRequestMethod(RequestMethod.GET, "/films/director/" + directorId + "?sortBy=" + sortBy);
            return directorService.getDirectorsFilms(directorId, sortBy);
        }
    }

    @GetMapping("/common")
    public List<Film> getCommonFilmsByFriends(@RequestParam("userId") Long userId,
                                              @RequestParam("friendId") Long friendId) {
        defaultLogger.logRequestMethod(RequestMethod.GET, "/films/common?userId=" + userId + "&friendId=" + friendId);
        return new ArrayList<>(filmService.getCommonFilmsByFriends(userId, friendId));
    }

    @GetMapping("/search")
    public List<Film> searchFilm(@RequestParam(required = false) String query,
                                 @RequestParam(required = false) List<String> by) {
        defaultLogger.logRequestMethod(RequestMethod.GET, "/films/search с параметрами 'query=" + query + "', 'by=" + by + "'");
        String director = null;
        String title = null;
        if (by.contains("director")) director = "director";
        if (by.contains("title")) title = "title";
        return new ArrayList<>(filmService.searchFilm(query, director, title));
    }
}