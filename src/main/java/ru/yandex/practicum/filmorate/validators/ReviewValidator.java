package ru.yandex.practicum.filmorate.validators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;

@Service
@Slf4j
public class ReviewValidator {

    public void validate(Review review) {
        validateReview(review);
        validateContentField(review);
        validateIsPositiveField(review);
        validateUserField(review);
        validateFilmField(review);
    }

    private void validateReview(Review review) {
        if (review == null) {
            log.warn("Экземпляр класса Review равно null!");
            throw new ValidationException("Данные отзыва не указаны.");
        }
    }

    private void validateContentField(Review review) {
        if (review.getContent() == null) {
            log.warn("Описание отзыва равно null!");
            throw new ValidationException("Описание равно null");
        }
        if (review.getContent().isBlank()) {
            log.warn("Отзыв не содержит описания!");
            throw new ValidationException("Описание отзыва не указано.");
        }
        if (review.getContent().length() > 200) {
            log.warn("Описание отзыва содержит более 200 символов!");
            throw new ValidationException("Описание отзыва не может превышать 200 символов.");
        }
    }

    private void validateIsPositiveField(Review review) {
        if (review.getIsPositive() == null) {
            log.warn("Не указан тип отзыва!");
            throw new ValidationException("Тип отзыва не указан.");
        }
    }

    private void validateUserField(Review review) {
        if (review.getUserId() == null) {
            log.warn("id пользователя равно null!");
            throw new ValidationException("id пользователя равно null");
        }
        if (review.getUserId() < 0) {
            log.warn("id пользователя меньше нуля!");
            throw new ArgumentNotFoundException("id пользователя меньше нуля.");
        }
    }

    private void validateFilmField(Review review) {
        if (review.getFilmId() == null) {
            log.warn("id фильма равно null!");
            throw new ValidationException("id фильма равно null");
        }
        if (review.getFilmId() < 0) {
            log.warn("id фильма меньше нуля!");
            throw new ArgumentNotFoundException("Значение id меньше нуля.");
        }
    }

}
