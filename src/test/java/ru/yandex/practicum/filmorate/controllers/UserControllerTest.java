package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.classes.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.validators.UserValidator;

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
        userController = new UserController(
                new UserService(
                        new InMemoryUserStorage(), new UserValidator()));
    }

    @Test
    void shouldCreateNewUser() {
        assertEquals(user, userController.postUser(user), "Новый пользователь не добавлен.");
    }

    @Test
    void shouldNotCreateThenNewUserHasSameEmail() {
        userController.postUser(user);
        User newUser = new User(
                "truelove@yandex.ru",
                "Truelove",
                "Vladimir",
                LocalDate.of(1990, 12, 8));
        assertThrows(ValidationException.class, () -> userController.postUser(newUser));
    }

    @Test
    void shouldUpdateUser() {
        userController.postUser(user);
        User newUser = new User(
                "truelove@yandex.ru",
                "Truelove",
                "Vladimir",
                LocalDate.of(1990, 12, 8));
        newUser.setId(user.getId());
        assertEquals(newUser, userController.putUser(newUser),
                "Данные пользователя не обновлены.");
    }

    @Test
    void shouldNotUpdateThenUserHasWrongId() {
        userController.postUser(user);
        User newUser = new User(
                "truelove@yandex.ru",
                "Truelove",
                "Vladimir",
                LocalDate.of(1990, 12, 8));
        newUser.setId(null);
        assertThrows(NullPointerException.class, () -> userController.putUser(newUser));
        newUser.setId(2L);
        assertThrows(NullPointerException.class, () -> userController.putUser(newUser));
    }

    @Test
    void shouldReturnUsers() {
        assertEquals(0, userController.getAllUsers().size(),
                "Список содержит пользователя(ей).");
        userController.postUser(user);
        assertEquals(1, userController.getAllUsers().size(),
                "Неверное количество фильмов в списке.");
    }

    @Test
    void shouldReturnByIdAndDeleteUser() {
        Long userId = user.getId();
        assertThrows(NullPointerException.class,
                () -> userController.getUserById(userId));
        userController.postUser(user);
        assertEquals(user, userController.getUserById(user.getId()),
                "Пользователь не соответствует ожидаемому значению.");
        userController.deleteUser(user);
        assertThrows(NullPointerException.class,
                () -> userController.getUserById(userId));
    }

    @Test
    void shouldCheckFriendsMethods() {
        userController.postUser(user);
        User newFriend = new User(
                "lier@yandex.ru",
                "Lier",
                "Vladimir",
                LocalDate.of(1990, 12, 8));
        User newUser = new User(
                "stranger@yandex.ru",
                "Stranger",
                "Shadow",
                LocalDate.of(1970, 5, 15));
        userController.postUser(newFriend);
        userController.postUser(newUser);
        Long userId = user.getId();
        Long friendId = newFriend.getId();
        Long newUserId = newUser.getId();
        userController.addFriend(userId, friendId);
        userController.addFriend(newUserId, friendId);
        assertTrue(userController.getFriendList(userId).contains(newFriend),
                "Друг не добавлен в список пользователя " + user.getLogin());
        assertTrue(userController.getFriendList(friendId).contains(user),
                "Друг не добавлен в список пользователя " + newFriend.getLogin());
        assertTrue(userController.getMutualFriendList(userId, newUserId).contains(newFriend),
                "Общий друг " + newFriend.getLogin() + " отсутствует");
        userController.deleteFriend(userId, friendId);
        assertFalse(userController.getFriendList(userId).contains(newFriend),
                "Друг не удален из списка пользователя " + user.getLogin());
        assertFalse(userController.getFriendList(friendId).contains(user),
                "Друг не удален из списка пользователя " + newFriend.getLogin());
    }
}
