package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.classes.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.classes.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.validators.FilmValidator;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import java.time.LocalDate;
import java.util.List;

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
        filmController = new FilmController(
                new FilmService(
                        new InMemoryFilmStorage(),
                        new UserService(new InMemoryUserStorage(), new UserValidator()),
                        new FilmValidator()));
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
        assertThrows(NullPointerException.class, () -> filmController.put(newFilm));
        newFilm.setId(2L);
        assertThrows(NullPointerException.class, () -> filmController.put(newFilm));
    }

    @Test
    void shouldReturnFilms() {
        assertEquals(0, filmController.getAllFilms().size(),
                "Список содержит фильм(ы).");
        filmController.post(film);
        assertEquals(1, filmController.getAllFilms().size(),
                "Неверное количество фильмов в списке.");
    }

    @Test
    void shouldReturnByIdAndDeleteFilm() {
        Long filmId = film.getId();
        assertThrows(NullPointerException.class,
                () -> filmController.getFilmById(filmId));
        filmController.post(film);
        assertEquals(film, filmController.getFilmById(film.getId()),
                "Фильм не соответствует ожидаемому значению.");
        filmController.delete(film);
        assertThrows(NullPointerException.class,
                () -> filmController.getFilmById(filmId));
    }

    @Test
    void shouldAddAndDeleteLike() {
        filmController.post(film);
        User user = new User(
                "truelove@yandex.ru",
                "Truelove",
                "Pavel",
                LocalDate.of(1990, 12, 8));
        filmController.getFilmService().getUserService().createUser(user);
        Long userId = user.getId();
        Long filmId = film.getId();
        filmController.addLike(filmId, userId);
        assertTrue(film.getLikes().contains(userId), "Лайк пользователя не добавлен.");
        filmController.deleteLike(filmId, userId);
        assertFalse(film.getLikes().contains(userId), "Лайк пользователя не удален.");
    }

    @Test
    void shouldGetPopularFilms() {
        filmController.post(film);
        User user = new User(
                "truelove@yandex.ru",
                "Truelove",
                "Pavel",
                LocalDate.of(1990, 12, 8));
        filmController.getFilmService().getUserService().createUser(user);
        Long userId = user.getId();
        Long filmId = film.getId();
        filmController.addLike(filmId, userId);
        List<Film> films = filmController.getPopularFilms(1L);
        assertEquals(1, films.size(), "Неверное количество фильмов в списке популярных.");
        assertEquals(film, films.get(0), "Фильм отсутствует в списке популярных.");

    }
}