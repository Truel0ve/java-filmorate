package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.Set;

public interface FriendStorage {

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    Set<Long> getAllFriends(Long userId);
}
