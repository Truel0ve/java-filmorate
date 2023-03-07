package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    Review createReview(Review review);
    void updateReview(Review review);
    void deleteReview(Long reviewId);
    Review getReviewById(Long reviewId);
    List<Review> getReviewsForFilm(Long filmId);
    List<Review> getAllReviews();

}
