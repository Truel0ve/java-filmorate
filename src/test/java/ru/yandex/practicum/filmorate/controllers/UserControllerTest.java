package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;
    private final User user = new User(
            "truelove@yandex.ru",
            "Truelove",
            "Pavel",
            LocalDate.of(1990, 12, 8));

    @BeforeEach
    void beforeEach() {
        userController = new UserController();
    }

    @Test
    void shouldCreateNewUser() {
        assertEquals(userController.createUser(user), user, "Новый пользователь не добавлен.");
        User newUser = new User(
                "truelove@yandex.ru",
                "Truelove",
                "Vladimir",
                LocalDate.of(1990, 12, 8));
        assertNotEquals(newUser, userController.createUser(newUser),
                "Новый пользователь добавлен.");
    }

    @Test
    void shouldUpdateUser() {
        userController.updateUser(user);
        User newUser = new User(
                "truelove@yandex.ru",
                "Truelove",
                "Vladimir",
                LocalDate.of(1990, 12, 8));
        assertEquals(newUser, userController.updateUser(newUser),
                "Данные пользователя не обновлены.");
    }

    @Test
    void shouldReturnNullThenUserHasWrongValue() {
        User newUser = new User(
                "truelove@yandex.ru",
                " ",
                "Vladimir",
                LocalDate.of(1990, 12, 8));
        assertNull(userController.createUser(newUser), "Новый пользователь добавлен.");
    }

    @Test
    void shouldReturnUsers() {
        assertEquals(0, userController.getUsers().size(),
                "Список содержит пользователя(ей).");
        userController.createUser(user);
        assertEquals(1, userController.getUsers().size(),
                "Неверное количество фильмов в списке.");
    }
}
