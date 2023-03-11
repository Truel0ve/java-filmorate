package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.database.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewStorage;
import ru.yandex.practicum.filmorate.validators.IdValidator;
import ru.yandex.practicum.filmorate.validators.ReviewValidator;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService implements ReviewStorage {
    private final ReviewDbStorage reviewStorage;
    private final EventService eventService;
    private final ReviewValidator reviewValidator;
    private final IdValidator idValidator;

    // Создать новый отзыв пользователя
    @Override
    public Review createReview(Review review) {
        reviewValidator.validate(review);
        Review createdReview = reviewStorage.createReview(review);
        eventService.addEvent(createdReview.getUserId(), createdReview.getReviewId(), "REVIEW", "ADD");
        return createdReview;
    }

    // Обновить отзыв пользователя
    @Override
    public Review updateReview(Review review) {
        idValidator.validateReviewId(review.getReviewId());
        reviewValidator.validate(review);
        Review updatedReview = reviewStorage.updateReview(review);
        eventService.addEvent(updatedReview.getUserId(), review.getReviewId(), "REVIEW", "UPDATE");
        return updatedReview;
    }

    // Удалить отзыв пользователя по ID
    @Override
    public void deleteReview(Long reviewId) {
        idValidator.validateReviewId(reviewId);
        Long userId = getReviewById(reviewId).getUserId();
        reviewStorage.deleteReview(reviewId);
        eventService.addEvent(userId, reviewId, "REVIEW", "REMOVE");
    }

    // Получить отзыв пользователя по ID
    @Override
    public Review getReviewById(Long reviewId) {
        idValidator.validateReviewId(reviewId);
        return reviewStorage.getReviewById(reviewId);
    }

    // Получить отзывы ко всем фильмам в количестве count
    @Override
    public List<Review> getAllReviews(Long count) {
        return reviewStorage.getAllReviews(count)
                .stream()
                .sorted(Comparator.comparing(Review::getUseful).reversed()) //Сортируем по убываю по полю useful
                .collect(Collectors.toList());
    }

    //Получить все отзывы к фильму по ID в количестве count
    @Override
    public List<Review> getAllReviewsForFilm(Long filmId, Long count) {
        idValidator.validateFilmId(filmId);
        return reviewStorage.getAllReviewsForFilm(filmId, count)
                .stream()
                .sorted(Comparator.comparing(Review::getUseful).reversed())
                .collect(Collectors.toList());
    }
}