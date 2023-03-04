package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

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
        assertThrows(ArgumentNotFoundException.class, () -> userController.putUser(newUser));
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
        assertThrows(ArgumentNotFoundException.class,
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

    @Test
    void shouldCheckGetEvents() {
        User newUser = User.builder()
                .email("lier@yandex.ru")
                .login("Lier")
                .name("Vladimir")
                .birthday(LocalDate.of(1990, 12, 8))
                .build();
        User newUser2 = User.builder()
                .email("stranger@yandex.ru")
                .login("Stranger")
                .name("Shadow")
                .birthday(LocalDate.of(1970, 5, 15))
                .build();
        User newUser3 = User.builder()
                .email("gggg@mail.ru")
                .login("Putnik")
                .name("Гриша")
                .birthday(LocalDate.of(1973, 10, 13))
                .build();
        userController.postUser(newUser);
        userController.postUser(newUser2);
        userController.postUser(newUser3);
        userController.addFriend(newUser.getId(), newUser2.getId());
        userController.addFriend(newUser2.getId(), newUser3.getId());

        List<Event> event = userController.getEvents(newUser.getId());
        assertEquals(event.get(0).getEventType(), "FRIEND");
    }
}