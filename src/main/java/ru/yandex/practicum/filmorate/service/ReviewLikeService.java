package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.storage.database.ReviewLikeDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.validators.IdValidator;

@Service
@AllArgsConstructor
public class ReviewLikeService implements ReviewLikeStorage {
    private final ReviewLikeDbStorage reviewLikeStorage;
    private final IdValidator idValidator;

    // Поставить лайк/дизлайк отзыву
    @Override
    public void addLikeOrDislike(Long reviewId, Long userId, String likeValue) {
        idValidator.validateReviewId(reviewId);
        idValidator.validateUserId(userId);
        reviewLikeStorage.addLikeOrDislike(reviewId, userId, validateLikeOrDislike(likeValue));
    }

    // Удалить лайк/дизлайк отзыву
    @Override
    public void deleteLikeOrDislike(Long reviewId, Long userId, String likeValue) {
        idValidator.validateReviewId(reviewId);
        idValidator.validateUserId(userId);
        reviewLikeStorage.deleteLikeOrDislike(reviewId, userId, validateLikeOrDislike(likeValue));
    }

    // Проверить параметр запроса
    private String validateLikeOrDislike(String likeValue) {
        switch (likeValue) {
            case "like":
                return "true";
            case "dislike":
                return "false";
            default:
                throw new ArgumentNotFoundException("Неверный параметр запроса");
        }
    }
}