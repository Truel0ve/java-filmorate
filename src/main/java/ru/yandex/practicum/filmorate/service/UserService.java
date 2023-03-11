package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.database.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.validators.IdValidator;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService implements UserStorage {
    private final UserDbStorage userStorage;
    private final UserValidator userValidator;
    private final IdValidator idValidator;

    // Создать нового пользователя
    @Override
    public User createUser(User user) {
        userValidator.validate(user);
        return userStorage.createUser(user);
    }

    // Обновить данные пользователя
    @Override
    public User updateUser(User user) {
        idValidator.validateUserId(user.getId());
        userValidator.validate(user);
        return userStorage.updateUser(user);
    }

    // Удалить пользователя
    @Override
    public void deleteUser(Long userId) {
        idValidator.validateUserId(userId);
        userStorage.deleteUser(userId);
    }

    // Получить данные пользователя по ID
    @Override
    public User getUserById(Long userId) {
        idValidator.validateUserId(userId);
        return userStorage.getUserById(userId);
    }

    // Получить список всех пользователей
    @Override
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }
}