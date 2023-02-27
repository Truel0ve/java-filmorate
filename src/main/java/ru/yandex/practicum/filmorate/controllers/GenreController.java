package ru.yandex.practicum.filmorate.controllers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/genres")
@Slf4j
@Getter
public class GenreController {
    private final FilmService filmService;

    @GetMapping
    public List<Genre> getAllGenres() {
        log.debug("Получен запрос GET по адресу: /genres");
        return filmService.getAllGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable("id") Long genreId) {
        log.debug("Получен запрос GET по адресу: /genres/" + genreId);
        return filmService.getGenreById(genreId);
    }
}
