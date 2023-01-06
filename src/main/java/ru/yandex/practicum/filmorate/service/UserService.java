package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.classes.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Getter
public class UserService implements UserStorage {
    private final InMemoryUserStorage userStorage;
    private final UserValidator userValidator;

    @Override
    public User createUser(User user) {
        userValidator.validate(user);
        return userStorage.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        validateUserId(user.getId());
        userValidator.validate(user);
        return userStorage.updateUser(user);
    }

    @Override
    public void deleteUser(User user) {
        validateUserId(user.getId());
        userStorage.deleteUser(user);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        validateUserId(userId);
        validateUserId(friendId);
        userStorage.addFriend(userId, friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        validateUserId(userId);
        validateUserId(friendId);
        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.getAllUsers().values());
    }

    public User getUserById(Long userId) {
        validateUserId(userId);
        return userStorage.getUserById(userId);
    }

    public List<User> getFriendList(Long userId) {
        validateUserId(userId);
        return userStorage.getFriends(userId).stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriendList(Long userId, Long otherId) {
        validateUserId(userId);
        validateUserId(otherId);
        Set<Long> friendSet = userStorage.getFriends(userId);
        Set<Long> otherSet = userStorage.getFriends(otherId);
        return friendSet.stream()
                .filter(otherSet::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public void validateUserId(Long userId) {
        if (userId == null || !userStorage.getAllUsers().containsKey(userId)) {
            throw new NullPointerException("ID пользователя не задан или отсутствует в базе.");
        }
    }
}