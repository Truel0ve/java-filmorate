package ru.yandex.practicum.filmorate.storage.interfaces;

public interface ReviewLikeStorage {
    void addLike(Long reviewId, Long userId, Boolean isLike);
    void deleteLike(Long reviewId, Long userId, Boolean isLike);
    void addDislike(Long reviewId, Long userId, Boolean isLike);
    void deleteDislike(Long reviewId, Long userId, Boolean isLike);

}
