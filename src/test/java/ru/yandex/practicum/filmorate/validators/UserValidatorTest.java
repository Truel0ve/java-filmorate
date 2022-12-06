package ru.yandex.practicum.filmorate.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utilities.ValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {
    private UserValidator userValidator;
    private User user;

    @BeforeEach
    void beforeEach() {
        userValidator = new UserValidator();
    }

    @Test
    void shouldNotValidateIfUserIsNull() {
        user = null;
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userValidator.validateUser(user));
        assertEquals("Данные пользователя не указаны.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfUserEmailIsNull() {
        user = new User(
                null,
                "Truelove",
                "Pavel",
                LocalDate.of(1990, 12, 8));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userValidator.validateUser(user));
        assertEquals("E-mail не указан.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfUserEmailIsBlank() {
        user = new User(
                " ",
                "Truelove",
                "Pavel",
                LocalDate.of(1990, 12, 8));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userValidator.validateUser(user));
        assertEquals("E-mail не указан.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfUserEmailHasNotDogChar() {
        user = new User(
                "truelove.yandex.ru",
                "Truelove",
                "Pavel",
                LocalDate.of(1990, 12, 8));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userValidator.validateUser(user));
        assertEquals("Неверно указан E-mail.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfUserLoginIsNull() {
        user = new User(
                "truelove@yandex.ru",
                null,
                "Pavel",
                LocalDate.of(1990, 12, 8));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userValidator.validateUser(user));
        assertEquals("Логин не указан.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfUserLoginIsBlank() {
        user = new User(
                "truelove@yandex.ru",
                " ",
                "Pavel",
                LocalDate.of(1990, 12, 8));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userValidator.validateUser(user));
        assertEquals("Логин не указан.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfUserLoginContainsSpace() {
        user = new User(
                "truelove@yandex.ru",
                "True love",
                "Pavel",
                LocalDate.of(1990, 12, 8));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userValidator.validateUser(user));
        assertEquals("Некорректный логин.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldSetUserNameIfUserNameIsNull() {
        user = new User(
                "truelove@yandex.ru",
                "Truelove",
                null,
                LocalDate.of(1990, 12, 8));
        assertDoesNotThrow(() -> userValidator.validateUser(user));
        assertEquals(user.getLogin(), user.getName(), "Имя пользователя и логин не совпадают");
    }

    @Test
    void shouldSetUserNameIfUserNameIsBlank() {
        user = new User(
                "truelove@yandex.ru",
                "Truelove",
                " ",
                LocalDate.of(1990, 12, 8));
        assertDoesNotThrow(() -> userValidator.validateUser(user));
        assertEquals(user.getLogin(), user.getName(), "Имя пользователя и логин не совпадают");
    }

    @Test
    void shouldNotValidateIfUserBirthdayIsNull() {
        user = new User(
                "truelove@yandex.ru",
                "Truelove",
                "Pavel",
                null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userValidator.validateUser(user));
        assertEquals("Дата рождения не указана.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfUserBirthdayIsAfterNow() {
        user = new User(
                "truelove@yandex.ru",
                "Truelove",
                "Pavel",
                LocalDate.of(2023, 12, 8));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userValidator.validateUser(user));
        assertEquals("Неверно указана дата рождения.",
                exception.getMessage(), "Неверный текст ошибки.");
    }
}
