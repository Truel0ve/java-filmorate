package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmControllerTest {
    private final FilmController filmController;

    private final Film film = Film.builder()
            .name("Терминатор")
            .description("История противостояния солдата Кайла Риза и киборга-терминатора, прибывших в 1984-й год из " +
                    "пост-апокалиптического будущего.")
            .releaseDate(LocalDate.of(1984, 10, 26))
            .duration(108)
            .mpa(new Mpa(4L, "R"))
            .build();

    @BeforeEach
    void beforeEach() {
        for (Film f : filmController.getAllFilms()) {
            filmController.deleteFilm(f.getId());
        }
    }

    @Test
    void shouldCreateNewFilm() {
        assertEquals(film, filmController.postFilm(film), "Новый фильм не добавлен.");
    }

    @Test
    void shouldNotCreateThenNewFilmHasSameName() {
        filmController.postFilm(film);
        Film newFilm = Film.builder()
                .name("Терминатор")
                .description("Фильм со Шварценеггером в главной роли.")
                .releaseDate(LocalDate.of(1984, 10, 26))
                .duration(108)
                .mpa(new Mpa(4L, "R"))
                .build();
        assertThrows(ValidationException.class, () -> filmController.postFilm(newFilm));
    }

    @Test
    void shouldUpdateFilm() {
        filmController.postFilm(film);
        Film newFilm = Film.builder()
                .name("Терминатор")
                .description("Фильм со Шварценеггером в главной роли.")
                .releaseDate(LocalDate.of(1984, 10, 26))
                .duration(108)
                .mpa(new Mpa(4L, "R"))
                .build();
        newFilm.setId(film.getId());
        assertEquals(newFilm, filmController.putFilm(newFilm),
                "Данные фильма не обновлены.");
    }

    @Test
    void shouldNotUpdateThenFilmHasWrongId() {
        filmController.postFilm(film);
        Film newFilm = Film.builder()
                .name("Терминатор")
                .description("Фильм со Шварценеггером в главной роли.")
                .releaseDate(LocalDate.of(1984, 10, 26))
                .duration(108)
                .mpa(new Mpa(4L, "R"))
                .build();
        newFilm.setId(null);
        assertThrows(NullPointerException.class, () -> filmController.putFilm(newFilm));
        newFilm.setId(2L);
        assertThrows(ArgumentNotFoundException.class, () -> filmController.putFilm(newFilm));
    }

    @Test
    void shouldReturnFilms() {
        assertEquals(0, filmController.getAllFilms().size(),
                "Список содержит фильм(ы).");
        filmController.postFilm(film);
        assertEquals(1, filmController.getAllFilms().size(),
                "Неверное количество фильмов в списке.");
    }

    @Test
    void shouldReturnByIdAndDeleteFilm() {
        filmController.postFilm(film);
        assertEquals(film, filmController.getFilmById(film.getId()),
                "Фильм не соответствует ожидаемому значению.");
        filmController.deleteFilm(film.getId());
        assertThrows(ArgumentNotFoundException.class,
                () -> filmController.getFilmById(film.getId()));
    }

    @Test
    void shouldAddAndDeleteLike() {
        User user = User.builder()
                .email("truelove@yandex.ru")
                .login("Truelove")
                .name("Pavel")
                .birthday(LocalDate.of(1990, 12, 8))
                .build();
        Long userId = filmController.getFilmService().getUserService().createUser(user).getId();
        Long filmId = filmController.postFilm(film).getId();
        filmController.addLike(filmId, userId);
        assertTrue(filmController.getFilmById(filmId).getLikes().contains(userId), "Лайк пользователя не добавлен.");
        filmController.deleteLike(filmId, userId);
        assertFalse(filmController.getFilmById(filmId).getLikes().contains(userId), "Лайк пользователя не удален.");
        filmController.getFilmService().getUserService().deleteUser(user.getId());
    }

    @Test
    void shouldGetPopularFilms() {
        User user = User.builder()
                .email("truelove@yandex.ru")
                .login("Truelove")
                .name("Pavel")
                .birthday(LocalDate.of(1990, 12, 8))
                .build();
        Long userId = filmController.getFilmService().getUserService().createUser(user).getId();
        Long filmId = filmController.postFilm(film).getId();
        filmController.addLike(filmId, userId);
        List<Film> films = filmController.getPopularFilms(1L, null, null);
        assertEquals(1, films.size(), "Неверное количество фильмов в списке популярных.");
        assertEquals(film, films.get(0), "Фильм отсутствует в списке популярных.");
    }
}