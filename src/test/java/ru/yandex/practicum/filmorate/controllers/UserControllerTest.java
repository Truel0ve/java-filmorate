package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utilities.ValidationException;

import java.time.LocalDate;
import java.util.HashMap;

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
    void shouldReturnNullIfThrowsException() {
        User newUser = new User(
                "truelove@yandex.ru",
                " ",
                "Vladimir",
                LocalDate.of(1990, 12, 8));
        assertThrows(ValidationException.class, () -> userController.validateUser(newUser));
        assertNull(userController.createUser(newUser), "Новый пользователь добавлен.");
    }

    @Test
    void shouldUpdateUser() {
        userController.updateUser(user);
        User newUser = new User(
                "truelove@yandex.ru",
                "Truelove",
                "Vladimir",
                LocalDate.of(1990, 12, 8));
        try {
            assertTrue(userController.validateUser(newUser));
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
        assertEquals(newUser, userController.updateUser(newUser),
                "Данные пользователя не обновлены.");
    }

    @Test
    void shouldReturnUserMap() {
        HashMap<Integer, User> newUsers = new HashMap<>();
        userController.createUser(user);
        assertEquals(1, userController.getUsers().size(), "Размер таблицы отличается");
        newUsers.put(user.getId(), user);
        assertEquals(newUsers, userController.getUsers());
    }
}
