package ru.yandex.practicum.filmorate.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.utility.DefaultLogger;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;
    private final DefaultLogger defaultLogger;

    @GetMapping
    public List<Genre> getAllGenres() {
        defaultLogger.logRequestMethod(RequestMethod.GET, "/genres");
        return genreService.getAllGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable("id") Long genreId) {
        defaultLogger.logRequestMethod(RequestMethod.GET, "/genres/" + genreId);
        return genreService.getGenreById(genreId);
    }
}