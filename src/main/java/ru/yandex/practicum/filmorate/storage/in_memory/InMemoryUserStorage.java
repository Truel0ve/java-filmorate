package ru.yandex.practicum.filmorate.storage.in_memory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long newId = 0L;

    @Override
    public User createUser(User user) {
        if (!users.isEmpty() && (users.values()
                    .stream()
                    .anyMatch(someUser -> someUser.getEmail().equals(user.getEmail())))) {
            throw new ValidationException("Пользователь с E-mail " + user.getEmail() + " уже есть в базе.");
        }
        user.setId(++newId);
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь с ID=" + user.getId() + ".");
        return user;
    }

    @Override
    public User updateUser(User user) {
        Long userId = user.getId();
        users.replace(userId, user);
        log.info("Внесены изменения в данные пользователя с ID=" + user.getId() + ".");
        return user;
    }

    @Override
    public void deleteUser(User user) {
        Long userId = user.getId();
        users.remove(userId);
        log.info("Пользователь с ID=" + user.getId() + " удален из базы.");
    }

    public Map<Long, User> getAllUsers() {
        return users;
    }

    public User getUserById(Long userId) {
        return users.get(userId);
    }

    public void addFriend(Long userId, Long friendId) {
        users.get(userId).getFriends().add(friendId);
        users.get(friendId).getFriends().add(userId);
        log.info("Пользователи с ID=" + userId + " и ID=" + friendId + " стали друзьями.");
    }

    public void deleteFriend(Long userId, Long friendId) {
        users.get(userId).getFriends().remove(friendId);
        users.get(friendId).getFriends().remove(userId);
        log.info("Пользователи с ID=" + userId + " и ID=" + friendId + " больше не являются друзьями.");
    }

    public Set<Long> getFriends(Long userId) {
        return users.get(userId).getFriends();
    }
}