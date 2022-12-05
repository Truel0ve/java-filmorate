package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utilities.ValidationException;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

class FilmControllerTest {
    private FilmController filmController;
    private final Film film = new Film(
            "Терминатор",
            "История противостояния солдата Кайла Риза и киборга-терминатора, прибывших в 1984-й год из " +
                    "пост-апокалиптического будущего.",
            LocalDate.of(1984, 10, 26),
            Duration.ofSeconds(6480));

    @BeforeEach
    void beforeEach() {
        filmController = new FilmController();
    }

    @Test
    void shouldCreateNewFilm() {
        assertEquals(filmController.createFilm(film), film, "Новый фильм не добавлен.");
        Film newFilm = new Film(
                "Терминатор",
                "Фильм со Шварценеггером в главной роли.",
                LocalDate.of(1984, 10, 26),
                Duration.ofSeconds(6480));
        assertNotEquals(newFilm, filmController.createFilm(newFilm),
                "Новый фильм добавлен.");
    }

    @Test
    void shouldReturnNullIfThrowsException() {
        Film newFilm = new Film(
                " ",
                "История противостояния солдата Кайла Риза и киборга-терминатора, прибывших в 1984-й год из " +
                        "пост-апокалиптического будущего.",
                LocalDate.of(1984, 10, 26),
                Duration.ofSeconds(6480));
        assertThrows(ValidationException.class, () -> filmController.validateFilm(newFilm));
        assertNull(filmController.createFilm(newFilm), "Новый фильм добавлен.");
    }

    @Test
    void shouldUpdateFilm() {
        filmController.updateFilm(film);
        Film newFilm = new Film(
                "Терминатор",
                "Фильм со Шварценеггером в главной роли.",
                LocalDate.of(1984, 10, 26),
                Duration.ofSeconds(6480));
        try {
            assertTrue(filmController.validateFilm(newFilm));
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
        assertEquals(newFilm, filmController.updateFilm(newFilm),
                "Данные фильма не обновлены.");
    }

    @Test
    void shouldReturnFilmMap() {
        HashMap<Integer, Film> newFilms = new HashMap<>();
        filmController.createFilm(film);
        assertEquals(1, filmController.getFilms().size(), "Размер таблицы отличается");
        newFilms.put(film.getId(), film);
        assertEquals(newFilms, filmController.getFilms());
    }
}
