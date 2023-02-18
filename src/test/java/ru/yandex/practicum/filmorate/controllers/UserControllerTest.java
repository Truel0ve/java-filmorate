package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    private final UserController userController;
    private final User user = User.builder()
            .email("truelove@yandex.ru")
            .login("Truelove")
            .name("Pavel")
            .birthday(LocalDate.of(1990, 12, 8))
            .build();

    @BeforeEach
    void beforeEach() {
        for (User u : userController.getAllUsers()) {
            userController.deleteUser(u);
        }
    }

    @Test
    void shouldCreateNewUser() {
        assertEquals(user, userController.postUser(user), "Новый пользователь не добавлен.");
    }

    @Test
    void shouldNotCreateThenNewUserHasSameEmail() {
        userController.postUser(user);
        User newUser = User.builder()
                .email("truelove@yandex.ru")
                .login("Truelove")
                .name("Vladimir")
                .birthday(LocalDate.of(1990, 12, 8))
                .build();
        assertThrows(ValidationException.class, () -> userController.postUser(newUser));
    }

    @Test
    void shouldUpdateUser() {
        userController.postUser(user);
        User newUser = User.builder()
                .email("truelove@yandex.ru")
                .login("Truelove")
                .name("Vladimir")
                .birthday(LocalDate.of(1990, 12, 8))
                .build();
        newUser.setId(user.getId());
        assertEquals(newUser, userController.putUser(newUser),
                "Данные пользователя не обновлены.");
    }

    @Test
    void shouldNotUpdateThenUserHasWrongId() {
        userController.postUser(user);
        User newUser = User.builder()
                .email("truelove@yandex.ru")
                .login("Truelove")
                .name("Vladimir")
                .birthday(LocalDate.of(1990, 12, 8))
                .build();
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
                "Неверное количество пользователей в списке.");
    }

    @Test
    void shouldReturnByIdAndDeleteUser() {
        userController.postUser(user);
        assertEquals(user, userController.getUserById(user.getId()),
                "Пользователь не соответствует ожидаемому значению.");
        userController.deleteUser(user);
        assertThrows(NullPointerException.class,
                () -> userController.getUserById(user.getId()));
    }

    @Test
    void shouldCheckFriendsMethods() {
        userController.postUser(user);
        User newFriend = User.builder()
                .email("lier@yandex.ru")
                .login("Lier")
                .name("Vladimir")
                .birthday(LocalDate.of(1990, 12, 8))
                .build();
        User newUser = User.builder()
                .email("stranger@yandex.ru")
                .login("Stranger")
                .name("Shadow")
                .birthday(LocalDate.of(1970, 5, 15))
                .build();
        userController.postUser(newFriend);
        userController.postUser(newUser);
        Long userId = user.getId();
        Long friendId = newFriend.getId();
        Long newUserId = newUser.getId();
        userController.addFriend(userId, friendId);
        assertTrue(userController.getFriendList(userId).contains(newFriend),
                "Друг не добавлен в список пользователя " + user.getLogin());
        assertFalse(userController.getFriendList(friendId).contains(user),
                "Друг добавлен в список пользователя " + newFriend.getLogin());
        userController.addFriend(friendId, userId);
        assertTrue(userController.getFriendList(friendId).contains(user),
                "Друг не добавлен в список пользователя " + newFriend.getLogin());
        userController.addFriend(newUserId, friendId);
        assertTrue(userController.getFriendList(newUserId).contains(newFriend),
                "Друг не добавлен в список пользователя " + newUser.getLogin());
        assertTrue(userController.getMutualFriendList(userId, newUserId).contains(newFriend),
                "Общий друг " + newFriend.getLogin() + " отсутствует");
        userController.deleteFriend(userId, friendId);
        assertFalse(userController.getFriendList(userId).contains(newFriend),
                "Друг не удален из списка пользователя " + user.getLogin());
        assertFalse(userController.getFriendList(friendId).contains(user),
                "Друг не удален из списка пользователя " + newFriend.getLogin());
    }
}