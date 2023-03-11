package ru.yandex.practicum.filmorate.validators;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;

@Component
public class ReviewValidator {

    public void validate(Review review) throws ValidationException, ArgumentNotFoundException {
        validateNotNull(review);
        validateContent(review);
        validateIsPositive(review);
        validateUserId(review);
        validateFilmId(review);
    }

    private void validateNotNull(Review review) throws ValidationException {
        if (review == null) {
            throw new ValidationException("Данные отзыва не указаны");
        }
    }

    private void validateContent(Review review) throws ValidationException {
        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new ValidationException("Содержание отзыва не указано");
        }
        if (review.getContent().length() > 200) {
            throw new ValidationException("Содержание отзыва не может превышать 200 символов");
        }
    }

    private void validateIsPositive(Review review) throws ValidationException {
        if (review.getIsPositive() == null) {
            throw new ValidationException("Тип отзыва не указан");
        }
    }

    private void validateUserId(Review review) throws ValidationException, ArgumentNotFoundException {
        if (review.getUserId() == null) {
            throw new ValidationException("ID пользователя не указан");
        }
        if (review.getUserId() <= 0) {
            throw new ArgumentNotFoundException("Значение ID пользователя не может быть меньше или равно 0");
        }
    }

    private void validateFilmId(Review review) throws ValidationException, ArgumentNotFoundException {
        if (review.getFilmId() == null) {
            throw new ValidationException("ID фильма не указан");
        }
        if (review.getFilmId() <= 0) {
            throw new ArgumentNotFoundException("Значение ID фильма не может быть меньше или равно 0");
        }
    }
}