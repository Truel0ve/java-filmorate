package ru.yandex.practicum.filmorate.storage.interfaces;

public interface FriendStorage {

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);
}
