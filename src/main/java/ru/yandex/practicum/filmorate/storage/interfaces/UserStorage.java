package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;


public interface UserStorage {

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(User user);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);
}