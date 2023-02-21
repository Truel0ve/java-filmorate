package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.database.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Getter
public class UserService implements UserStorage {
    private final UserDbStorage userStorage;
    private final UserValidator userValidator;

    // Создать нового пользователя
    @Override
    public User createUser(User user) {
        userValidator.validate(user);
        return userStorage.createUser(user);
    }

    // Обновить данные пользователя
    @Override
    public User updateUser(User user) {
        validateUserId(user.getId());
        userValidator.validate(user);
        return userStorage.updateUser(user);
    }

    // Удалить пользователя
    @Override
    public void deleteUser(User user) {
        validateUserId(user.getId());
        userStorage.deleteUser(user);
    }

    // Получить данные пользователя по ID
    public User getUserById(Long userId) {
        validateUserId(userId);
        return userStorage.getUserById(userId);
    }

    // Получить список всех пользователей
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    // Добавить пользователя в друзья
    public void addFriend(Long userId, Long friendId) {
        validateUserId(userId);
        validateUserId(friendId);
        userStorage.getFriendDbStorage().addFriend(userId, friendId);
    }

    // Удалить пользователя из друзей
    public void deleteFriend(Long userId, Long friendId) {
        validateUserId(userId);
        validateUserId(friendId);
        userStorage.getFriendDbStorage().deleteFriend(userId, friendId);
    }

    // Получить список всех друзей пользователя по ID
    public List<User> getFriendList(Long userId) {
        validateUserId(userId);
        return userStorage.getFriendDbStorage().getAllFriends(userId).stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    // Получить список общих друзей пользователей с userId и otherId
    public List<User> getCommonFriendList(Long userId, Long otherId) {
        validateUserId(userId);
        validateUserId(otherId);
        Set<Long> friendSet = userStorage.getFriendDbStorage().getAllFriends(userId);
        Set<Long> otherSet = userStorage.getFriendDbStorage().getAllFriends(otherId);
        return friendSet.stream()
                .filter(otherSet::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    // Проверить корректность передаваемого ID пользователя
    public void validateUserId(Long userId) {
        if (userId == null || userStorage.getUserById(userId) == null) {
            throw new NullPointerException("ID пользователя не задан или отсутствует в базе.");
        }
    }
}