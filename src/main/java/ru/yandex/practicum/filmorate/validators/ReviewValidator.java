package ru.yandex.practicum.filmorate.validators;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;

@Service
public class ReviewValidator {

    public void validate(Review review) {
        validateReviewNotNull(review);
        validateReviewContent(review);
        validateReviewIsPositiveNotNull(review);
        validateReviewUserNotNullOrNegative(review);
        validateReviewFilmNotNullOrNegative(review);
    }

    private void validateReviewNotNull(Review review) throws ValidationException {
        if (review == null) {
            throw new ValidationException("Данные отзыва не указаны.");
        }
    }

    private void validateReviewContent(Review review) throws ValidationException {
        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new ValidationException("Описание отзыва не указано.");
        }
        if (review.getContent().length() > 200) {
            throw new ValidationException("Описание отзыва не может превышать 200 символов.");
        }
    }

    private void validateReviewIsPositiveNotNull(Review review) throws ValidationException {
        if (review.getIsPositive() == null) {
            throw new ValidationException("Тип отзыва не указан.");
        }
    }

    private void validateReviewUserNotNullOrNegative(Review review) throws ValidationException {
        if (review.getUserId() == null || review.getUserId() < 0) {
            throw new ValidationException("Не указан id пользователя или значение id меньше нуля.");
        }
    }

    private void validateReviewFilmNotNullOrNegative(Review review) throws ValidationException {
        if (review.getFilmId() == null || review.getFilmId() < 0) {
            throw new ValidationException("Не указан id фильма или значение id меньше нуля.");
        }
    }

}
