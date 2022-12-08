package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
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
        assertEquals(user, userController.createUser(user), "Новый пользователь не добавлен.");
    }

    @Test
    void shouldNotCreateThenNewUserHasSameEmail() {
        userController.createUser(user);
        User newUser = new User(
                "truelove@yandex.ru",
                "Truelove",
                "Vladimir",
                LocalDate.of(1990, 12, 8));
        assertThrows(ResponseStatusException.class, () -> userController.createUser(newUser));
    }

    @Test
    void shouldUpdateUser() {
        userController.createUser(user);
        User newUser = new User(
                "truelove@yandex.ru",
                "Truelove",
                "Vladimir",
                LocalDate.of(1990, 12, 8));
        newUser.setId(user.getId());
        assertEquals(newUser, userController.updateUser(newUser),
                "Данные пользователя не обновлены.");
    }

    @Test
    void shouldNotUpdateThenUserHasWrongId() {
        userController.createUser(user);
        User newUser = new User(
                "truelove@yandex.ru",
                "Truelove",
                "Vladimir",
                LocalDate.of(1990, 12, 8));
        newUser.setId(null);
        assertThrows(ResponseStatusException.class, () -> userController.updateUser(newUser));
        newUser.setId(2);
        assertThrows(ResponseStatusException.class, () -> userController.updateUser(newUser));
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
