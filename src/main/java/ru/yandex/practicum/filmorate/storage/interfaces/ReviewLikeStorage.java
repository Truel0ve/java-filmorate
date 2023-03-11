package ru.yandex.practicum.filmorate.storage.interfaces;

public interface ReviewLikeStorage {

    void addLikeOrDislike(Long reviewId, Long userId, String likeValue);

    void deleteLikeOrDislike(Long reviewId, Long userId, String likeValue);
}