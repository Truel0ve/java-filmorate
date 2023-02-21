package ru.yandex.practicum.filmorate.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidatorTest {
    private FilmValidator filmValidator;
    private Film film;

    @BeforeEach
    void beforeEach() {
        filmValidator = new FilmValidator();
    }

    @Test
    void shouldNotValidateIfFilmIsNull() {
        film = null;
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmValidator.validate(film));
        assertEquals("Данные фильма не указаны.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfFilmNameIsNull() {
        film = Film.builder()
                .name(null)
                .description("История противостояния солдата Кайла Риза и киборга-терминатора, прибывших в 1984-й год из " +
                        "пост-апокалиптического будущего.")
                .releaseDate(LocalDate.of(1984, 10, 26))
                .duration(108)
                .build();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmValidator.validate(film));
        assertEquals("Название фильма не указано.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfFilmNameIsBlank() {
        film = Film.builder()
                .name(" ")
                .description("История противостояния солдата Кайла Риза и киборга-терминатора, прибывших в 1984-й год из " +
                        "пост-апокалиптического будущего.")
                .releaseDate(LocalDate.of(1984, 10, 26))
                .duration(108)
                .build();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmValidator.validate(film));
        assertEquals("Название фильма не указано.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfFilmDescriptionIsNull() {
        film = Film.builder()
                .name("Терминатор")
                .description(null)
                .releaseDate(LocalDate.of(1984, 10, 26))
                .duration(108)
                .build();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmValidator.validate(film));
        assertEquals("Описание фильма не указано.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfFilmDescriptionIsBlank() {
        film = Film.builder()
                .name("Терминатор")
                .description(" ")
                .releaseDate(LocalDate.of(1984, 10, 26))
                .duration(108)
                .build();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmValidator.validate(film));
        assertEquals("Описание фильма не указано.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfFilmDescriptionIsMoreThen200() {
        film = Film.builder()
                .name("Терминатор")
                .description("История противостояния солдата Кайла Риза и киборга-терминатора, прибывших в 1984-й год из " +
                        "пост-апокалиптического будущего, где миром правят машины-убийцы, а человечество находится на " +
                        "грани вымирания. Цель киборга: убить девушку по имени Сара Коннор, чей ещё нерождённый сын к " +
                        "2029 году выиграет войну человечества с машинами. Цель Риза: спасти Сару и остановить " +
                        "Терминатора любой ценой.")
                .releaseDate(LocalDate.of(1984, 10, 26))
                .duration(108)
                .build();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmValidator.validate(film));
        assertEquals("Описание фильма не может превышать 200 символов.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldValidateIfFilmDescriptionIs200() {
        film = Film.builder()
                .name("Терминатор")
                .description("История противостояния солдата Кайла Риза и киборга-терминатора, прибывших в 1984-й год" +
                        " из пост-апокалиптического будущего, где миром правят машины-убийцы, а человечество находится" +
                        " на грани вымирания.")
                .releaseDate(LocalDate.of(1984, 10, 26))
                .duration(108)
                .build();
        assertEquals(200, film.getDescription().length());
        assertDoesNotThrow(() -> filmValidator.validate(film));
    }

    @Test
    void shouldNotValidateIfFilmReleaseDateIsNull() {
        film = Film.builder()
                .name("Терминатор")
                .description("История противостояния солдата Кайла Риза и киборга-терминатора, прибывших в 1984-й год из " +
                        "пост-апокалиптического будущего.")
                .releaseDate(null)
                .duration(108)
                .build();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmValidator.validate(film));
        assertEquals("Дата релиза не указана.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfFilmReleaseDateIsLess1895_12_28() {
        film = Film.builder()
                .name("Терминатор")
                .description("История противостояния солдата Кайла Риза и киборга-терминатора, прибывших в 1984-й год из " +
                        "пост-апокалиптического будущего.")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(108)
                .build();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmValidator.validate(film));
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldValidateIfFilmReleaseDateIs1895_12_28() {
        film = Film.builder()
                .name("Терминатор")
                .description("История противостояния солдата Кайла Риза и киборга-терминатора, прибывших в 1984-й год из " +
                        "пост-апокалиптического будущего.")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(108)
                .build();
        assertDoesNotThrow(() -> filmValidator.validate(film));
    }

    @Test
    void shouldNotValidateIfFilmDurationIsNull() {
        film = Film.builder()
                .name("Терминатор")
                .description("История противостояния солдата Кайла Риза и киборга-терминатора, прибывших в 1984-й год из " +
                        "пост-апокалиптического будущего.")
                .releaseDate(LocalDate.of(1984, 10, 26))
                .duration(null)
                .build();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmValidator.validate(film));
        assertEquals("Продолжительность фильма не указана.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfFilmDurationIsZero() {
        film = Film.builder()
                .name("Терминатор")
                .description("История противостояния солдата Кайла Риза и киборга-терминатора, прибывших в 1984-й год из " +
                        "пост-апокалиптического будущего.")
                .releaseDate(LocalDate.of(1984, 10, 26))
                .duration(0)
                .build();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmValidator.validate(film));
        assertEquals("Продолжительность фильма не может быть меньше или равна 0.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfFilmDurationIsNegative() {
        film = Film.builder()
                .name("Терминатор")
                .description("История противостояния солдата Кайла Риза и киборга-терминатора, прибывших в 1984-й год из " +
                        "пост-апокалиптического будущего.")
                .releaseDate(LocalDate.of(1984, 10, 26))
                .duration(-100)
                .build();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmValidator.validate(film));
        assertEquals("Продолжительность фильма не может быть меньше или равна 0.",
                exception.getMessage(), "Неверный текст ошибки.");
    }
}
