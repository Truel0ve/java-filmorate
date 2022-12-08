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
        assertEquals(user, userController.post(user), "Новый пользователь не добавлен.");
    }

    @Test
    void shouldNotCreateThenNewUserHasSameEmail() {
        userController.post(user);
        User newUser = new User(
                "truelove@yandex.ru",
                "Truelove",
                "Vladimir",
                LocalDate.of(1990, 12, 8));
        assertThrows(ResponseStatusException.class, () -> userController.post(newUser));
    }

    @Test
    void shouldUpdateUser() {
        userController.post(user);
        User newUser = new User(
                "truelove@yandex.ru",
                "Truelove",
                "Vladimir",
                LocalDate.of(1990, 12, 8));
        newUser.setId(user.getId());
        assertEquals(newUser, userController.put(newUser),
                "Данные пользователя не обновлены.");
    }

    @Test
    void shouldNotUpdateThenUserHasWrongId() {
        userController.post(user);
        User newUser = new User(
                "truelove@yandex.ru",
                "Truelove",
                "Vladimir",
                LocalDate.of(1990, 12, 8));
        newUser.setId(null);
        assertThrows(ResponseStatusException.class, () -> userController.put(newUser));
        newUser.setId(2);
        assertThrows(ResponseStatusException.class, () -> userController.put(newUser));
    }

    @Test
    void shouldReturnUsers() {
        assertEquals(0, userController.get().size(),
                "Список содержит пользователя(ей).");
        userController.post(user);
        assertEquals(1, userController.get().size(),
                "Неверное количество фильмов в списке.");
    }
}
