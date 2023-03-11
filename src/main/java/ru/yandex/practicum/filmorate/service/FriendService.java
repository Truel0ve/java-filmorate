package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.database.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FriendStorage;
import ru.yandex.practicum.filmorate.validators.IdValidator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FriendService implements FriendStorage {
    private final FriendDbStorage friendStorage;
    private final UserService userService;
    private final EventService eventService;
    private final IdValidator idValidator;

    // Добавить пользователя в друзья
    @Override
    public void addFriend(Long userId, Long friendId) {
        idValidator.validateUserId(userId);
        idValidator.validateUserId(friendId);
        friendStorage.addFriend(userId, friendId);
        eventService.addEvent(userId, friendId, "FRIEND", "ADD");
    }

    // Удалить пользователя из друзей
    @Override
    public void deleteFriend(Long userId, Long friendId) {
        idValidator.validateUserId(userId);
        idValidator.validateUserId(friendId);
        friendStorage.deleteFriend(userId,friendId);
        eventService.addEvent(userId, friendId, "FRIEND", "REMOVE");
    }

    // Получить сет-лист с ID друзей пользователя
    @Override
    public Set<Long> getAllFriends(Long userId) {
        idValidator.validateUserId(userId);
        return friendStorage.getAllFriends(userId);
    }

    // Получить список друзей пользователя
    public List<User> getFriendList(Long userId) {
        return getAllFriends(userId).stream()
                .map(userService::getUserById)
                .collect(Collectors.toList());
    }

    // Получить список общих друзей пользователей с userId и otherId
    public List<User> getCommonFriendList(Long userId, Long otherId) {
        Set<Long> friendSet = getAllFriends(userId);
        Set<Long> otherSet = getAllFriends(otherId);
        return friendSet.stream()
                .filter(otherSet::contains)
                .map(userService::getUserById)
                .collect(Collectors.toList());
    }
}