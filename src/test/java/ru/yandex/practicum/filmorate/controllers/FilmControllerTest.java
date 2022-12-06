package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

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
    void shouldUpdateFilm() {
        filmController.updateFilm(film);
        Film newFilm = new Film(
                "Терминатор",
                "Фильм со Шварценеггером в главной роли.",
                LocalDate.of(1984, 10, 26),
                Duration.ofSeconds(6480));
        assertEquals(newFilm, filmController.updateFilm(newFilm),
                "Данные фильма не обновлены.");
    }

    @Test
    void shouldReturnNullThenFilmHasWrongValue() {
        Film newFilm = new Film(
                " ",
                "История противостояния солдата Кайла Риза и киборга-терминатора, прибывших в 1984-й год из " +
                        "пост-апокалиптического будущего.",
                LocalDate.of(1984, 10, 26),
                Duration.ofSeconds(6480));
        assertNull(filmController.createFilm(newFilm), "Новый фильм добавлен.");
    }

    @Test
    void shouldReturnFilms() {
        assertEquals(0, filmController.getFilms().size(),
                "Список содержит фильм(ы).");
        filmController.createFilm(film);
        assertEquals(1, filmController.getFilms().size(),
                "Неверное количество фильмов в списке.");
    }
}
