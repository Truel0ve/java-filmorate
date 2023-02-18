package ru.yandex.practicum.filmorate.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
                () -> userValidator.validate(user));
        assertEquals("Данные пользователя не указаны.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfUserEmailIsNull() {
        user = User.builder()
                .email(null)
                .login("Truelove")
                .name("Pavel")
                .birthday(LocalDate.of(1990, 12, 8))
                .build();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userValidator.validate(user));
        assertEquals("E-mail не указан.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfUserEmailIsBlank() {
        user = User.builder()
                .email(" ")
                .login("Truelove")
                .name("Pavel")
                .birthday(LocalDate.of(1990, 12, 8))
                .build();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userValidator.validate(user));
        assertEquals("E-mail не указан.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfUserEmailHasNotDogChar() {
        user = User.builder()
                .email("truelove.yandex.ru")
                .login("Truelove")
                .name("Pavel")
                .birthday(LocalDate.of(1990, 12, 8))
                .build();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userValidator.validate(user));
        assertEquals("Неверно указан E-mail: " + user.getEmail(),
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfUserLoginIsNull() {
        user = User.builder()
                .email("truelove@yandex.ru")
                .login(null)
                .name("Pavel")
                .birthday(LocalDate.of(1990, 12, 8))
                .build();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userValidator.validate(user));
        assertEquals("Логин не указан.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfUserLoginIsBlank() {
        user = User.builder()
                .email("truelove@yandex.ru")
                .login(" ")
                .name("Pavel")
                .birthday(LocalDate.of(1990, 12, 8))
                .build();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userValidator.validate(user));
        assertEquals("Логин не указан.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfUserLoginContainsSpace() {
        user = User.builder()
                .email("truelove@yandex.ru")
                .login("True love")
                .name("Pavel")
                .birthday(LocalDate.of(1990, 12, 8))
                .build();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userValidator.validate(user));
        assertEquals("Некорректный логин: " + user.getLogin(),
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldSetUserNameIfUserNameIsNull() {
        user = User.builder()
                .email("truelove@yandex.ru")
                .login("Truelove")
                .name(null)
                .birthday(LocalDate.of(1990, 12, 8))
                .build();
        assertDoesNotThrow(() -> userValidator.validate(user));
        assertEquals(user.getLogin(), user.getName(), "Имя пользователя и логин не совпадают");
    }

    @Test
    void shouldSetUserNameIfUserNameIsBlank() {
        user = User.builder()
                .email("truelove@yandex.ru")
                .login("Truelove")
                .name(" ")
                .birthday(LocalDate.of(1990, 12, 8))
                .build();
        assertDoesNotThrow(() -> userValidator.validate(user));
        assertEquals(user.getLogin(), user.getName(), "Имя пользователя и логин не совпадают");
    }

    @Test
    void shouldNotValidateIfUserBirthdayIsNull() {
        user = User.builder()
                .email("truelove@yandex.ru")
                .login("Truelove")
                .name("Pavel")
                .birthday(null)
                .build();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userValidator.validate(user));
        assertEquals("Дата рождения не указана.",
                exception.getMessage(), "Неверный текст ошибки.");
    }

    @Test
    void shouldNotValidateIfUserBirthdayIsAfterNow() {
        user = User.builder()
                .email("truelove@yandex.ru")
                .login("Truelove")
                .name("Pavel")
                .birthday(LocalDate.of(2023, 12, 8))
                .build();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userValidator.validate(user));
        assertEquals("Неверно указана дата рождения: " + user.getBirthday().
                        format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                exception.getMessage(), "Неверный текст ошибки.");
    }
}
