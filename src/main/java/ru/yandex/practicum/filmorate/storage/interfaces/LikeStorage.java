package ru.yandex.practicum.filmorate.storage.interfaces;

public interface LikeStorage {

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);
}
