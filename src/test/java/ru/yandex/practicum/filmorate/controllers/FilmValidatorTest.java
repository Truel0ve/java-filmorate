package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utilities.ValidationException;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmValidatorTest {
    private FilmController filmController;
    private Film film;

    @BeforeEach
    void beforeEach() {
        filmController = new FilmController();
    }

    @Test
    void shouldNotValidateIfFilmIsNull() {
        film = null;
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.validateFilm(film));
        assertEquals("Данные фильма не указаны.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfFilmNameIsNull() {
        film = new Film(
                null,
                "История противостояния солдата Кайла Риза и киборга-терминатора, прибывших в 1984-й год из " +
                        "пост-апокалиптического будущего.",
                LocalDate.of(1984, 10, 26),
                Duration.ofSeconds(6480));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.validateFilm(film));
        assertEquals("Название фильма не указано.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfFilmNameIsBlank() {
        film = new Film(
                " ",
                "История противостояния солдата Кайла Риза и киборга-терминатора, прибывших в 1984-й год из " +
                        "пост-апокалиптического будущего.",
                LocalDate.of(1984, 10, 26),
                Duration.ofSeconds(6480));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.validateFilm(film));
        assertEquals("Название фильма не указано.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfFilmDescriptionIsNull() {
        film = new Film(
                "Терминатор",
                null,
                LocalDate.of(1984, 10, 26),
                Duration.ofSeconds(6480));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.validateFilm(film));
        assertEquals("Описание фильма не указано.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfFilmDescriptionIsBlank() {
        film = new Film(
                "Терминатор",
                " ",
                LocalDate.of(1984, 10, 26),
                Duration.ofSeconds(6480));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.validateFilm(film));
        assertEquals("Описание фильма не указано.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfFilmDescriptionIsMoreThen200() {
        film = new Film(
                "Терминатор",
                "История противостояния солдата Кайла Риза и киборга-терминатора, прибывших в 1984-й год из " +
                        "пост-апокалиптического будущего, где миром правят машины-убийцы, а человечество находится на " +
                        "грани вымирания. Цель киборга: убить девушку по имени Сара Коннор, чей ещё нерождённый сын к " +
                        "2029 году выиграет войну человечества с машинами. Цель Риза: спасти Сару и остановить " +
                        "Терминатора любой ценой.",
                LocalDate.of(1984, 10, 26),
                Duration.ofSeconds(6480));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.validateFilm(film));
        assertEquals("Описание фильма не может превышать 200 символов.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldValidateIfFilmDescriptionIs200() {
        film = new Film(
                "Терминатор",
                "История противостояния солдата Кайла Риза и киборга-терминатора, прибывших в 1984-й год" +
                        " из пост-апокалиптического будущего, где миром правят машины-убийцы, а человечество находится" +
                        " на грани вымирания.",
                LocalDate.of(1984, 10, 26),
                Duration.ofSeconds(6480));
        try {
            assertFalse(filmController.validateFilm(film));
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void shouldNotValidateIfFilmReleaseDateIsNull() {
        film = new Film(
                "Терминатор",
                "История противостояния солдата Кайла Риза и киборга-терминатора, прибывших в 1984-й год из " +
                        "пост-апокалиптического будущего.",
                null,
                Duration.ofSeconds(6480));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.validateFilm(film));
        assertEquals("Дата релиза не указана.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfFilmReleaseDateIsLess1895_12_28() {
        film = new Film(
                "Терминатор",
                "История противостояния солдата Кайла Риза и киборга-терминатора, прибывших в 1984-й год из " +
                        "пост-апокалиптического будущего.",
                LocalDate.of(1895, 12, 27),
                Duration.ofSeconds(6480));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.validateFilm(film));
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldValidateIfFilmReleaseDateIs1895_12_28() {
        film = new Film(
                "Терминатор",
                "История противостояния солдата Кайла Риза и киборга-терминатора, прибывших в 1984-й год из " +
                        "пост-апокалиптического будущего.",
                LocalDate.of(1895, 12, 28),
                Duration.ofSeconds(6480));
        try {
            assertFalse(filmController.validateFilm(film));
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void shouldNotValidateIfFilmDurationIsNull() {
        film = new Film(
                "Терминатор",
                "История противостояния солдата Кайла Риза и киборга-терминатора, прибывших в 1984-й год из " +
                        "пост-апокалиптического будущего.",
                LocalDate.of(1984, 10, 26),
                null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.validateFilm(film));
        assertEquals("Продолжительность фильма не указана.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfFilmDurationIsZero() {
        film = new Film(
                "Терминатор",
                "История противостояния солдата Кайла Риза и киборга-терминатора, прибывших в 1984-й год из " +
                        "пост-апокалиптического будущего.",
                LocalDate.of(1984, 10, 26),
                Duration.ofSeconds(0));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.validateFilm(film));
        assertEquals("Продолжительность фильма не может быть меньше или равна 0.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfFilmDurationIsNegative() {
        film = new Film(
                "Терминатор",
                "История противостояния солдата Кайла Риза и киборга-терминатора, прибывших в 1984-й год из " +
                        "пост-апокалиптического будущего.",
                LocalDate.of(1984, 10, 26),
                Duration.ofSeconds(-100));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.validateFilm(film));
        assertEquals("Продолжительность фильма не может быть меньше или равна 0.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldValidateFilm() {
        film = new Film(
                "Терминатор",
                "История противостояния солдата Кайла Риза и киборга-терминатора, прибывших в 1984-й год из " +
                        "пост-апокалиптического будущего.",
                LocalDate.of(1984, 10, 26),
                Duration.ofSeconds(6480));
        try {
            assertFalse(filmController.validateFilm(film));
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }
}
