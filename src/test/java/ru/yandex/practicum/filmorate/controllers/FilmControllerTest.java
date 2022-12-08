package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;
    private final Film film = new Film(
            "Терминатор",
            "История противостояния солдата Кайла Риза и киборга-терминатора, прибывших в 1984-й год из " +
                    "пост-апокалиптического будущего.",
            LocalDate.of(1984, 10, 26),
            108);

    @BeforeEach
    void beforeEach() {
        filmController = new FilmController();
    }

    @Test
    void shouldCreateNewFilm() {
        assertEquals(film, filmController.post(film), "Новый фильм не добавлен.");
    }

    @Test
    void shouldNotCreateThenNewFilmHasSameName() {
        filmController.post(film);
        Film newFilm = new Film(
                "Терминатор",
                "Фильм со Шварценеггером в главной роли.",
                LocalDate.of(1984, 10, 26),
                108);
        assertThrows(ResponseStatusException.class, () -> filmController.post(newFilm));
    }

    @Test
    void shouldUpdateFilm() {
        filmController.post(film);
        Film newFilm = new Film(
                "Терминатор",
                "Фильм со Шварценеггером в главной роли.",
                LocalDate.of(1984, 10, 26),
                108);
        newFilm.setId(film.getId());
        assertEquals(newFilm, filmController.put(newFilm),
                "Данные фильма не обновлены.");
    }

    @Test
    void shouldNotUpdateThenFilmHasWrongId() {
        filmController.post(film);
        Film newFilm = new Film(
                "Терминатор",
                "Фильм со Шварценеггером в главной роли.",
                LocalDate.of(1984, 10, 26),
                108);
        newFilm.setId(null);
        assertThrows(ResponseStatusException.class, () -> filmController.put(newFilm));
        newFilm.setId(2);
        assertThrows(ResponseStatusException.class, () -> filmController.put(newFilm));
    }

    @Test
    void shouldReturnFilms() {
        assertEquals(0, filmController.get().size(),
                "Список содержит фильм(ы).");
        filmController.post(film);
        assertEquals(1, filmController.get().size(),
                "Неверное количество фильмов в списке.");
    }
}
