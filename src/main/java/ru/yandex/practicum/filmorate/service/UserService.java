package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.classes.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
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
        try {
            validateId(user.getId());
            userValidator.validate(user);
            return userStorage.updateUser(user);
        } catch (IllegalArgumentException | ValidationException e) {
            log.warn(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void deleteUser(User user) {
        try {
            validateId(user.getId());
            userStorage.deleteUser(user);
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        try {
            validateId(userId);
            validateId(friendId);
            userStorage.addFriend(userId, friendId);
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        try {
            validateId(userId);
            validateId(friendId);
            userStorage.deleteFriend(userId, friendId);
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.getAllUsers().values());
    }

    public User getUserById(Long userId) {
        validateId(userId);
        return userStorage.getUserById(userId);
    }

    public List<User> getFriendList(Long userId) {
        validateId(userId);
        return userStorage.getFriends(userId).stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getMutualFriendList(Long userId, Long otherId) {
        validateId(userId);
        validateId(otherId);
        Set<Long> friendSet = userStorage.getFriends(userId);
        Set<Long> otherSet = userStorage.getFriends(otherId);
        return friendSet.stream()
                .filter(otherSet::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    private void validateId(Long userId) {
        if (userId == null || !userStorage.getAllUsers().containsKey(userId)) {
            log.warn("ID пользователя не задан или отсутствует в базе.");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}