package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.database.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.database.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.database.ReviewLikeDbStorage;
import ru.yandex.practicum.filmorate.storage.database.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewStorage;
import ru.yandex.practicum.filmorate.validators.ReviewValidator;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService implements ReviewStorage, ReviewLikeStorage {
    private final ReviewDbStorage reviewDbStorage;
    private final ReviewLikeDbStorage reviewLikeDbStorage;
    private final ReviewValidator reviewValidator;
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    @Override
    public Review createReview(Review review) {
        reviewValidator.validate(review); //Проверка переменных экземпляра класса Review
        //checkReviewInDb(review.getId()); //Проверка наличия отзыва в БД по его id

        return reviewDbStorage.createReview(review);
    }

    @Override
    public Review updateReview(Review review) {
        reviewValidator.validate(review);
        checkReviewInDb(review.getReviewId());

        return reviewDbStorage.updateReview(review);
    }

    @Override
    public void deleteReview(Long reviewId) {
        checkReviewInDb(reviewId);
        reviewDbStorage.deleteReview(reviewId);
    }

    @Override
    public Review getReviewById(Long reviewId) {
        checkReviewInDb(reviewId);
        Review review = reviewDbStorage.getReviewById(reviewId);
        Long rating = reviewLikeDbStorage.getUseful(reviewId);
        review.setUseful(rating);

        return review;
    }

    @Override
    public List<Review> getAllReviewsFromFilm(Long filmId, Long count) {

        return reviewDbStorage.getAllReviewsFromFilm(filmId, count)
                .stream()
                .peek(review -> review.setUseful(reviewLikeDbStorage.getUseful(review.getReviewId())))
                .sorted(Comparator.comparing(Review::getUseful).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<Review> getAllReviews() {

        return reviewDbStorage.getAllReviews()
                .stream()
                .peek(review -> review.setUseful(reviewLikeDbStorage.getUseful(review.getReviewId())))
                .sorted(Comparator.comparing(Review::getUseful).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public void addLike(Long reviewId, Long userId, Boolean isLike) {
        checkReviewInDb(reviewId);
        checkUserInDb(userId);
        reviewLikeDbStorage.addLike(reviewId, userId, isLike);
    }

    @Override
    public void deleteLike(Long reviewId, Long userId, Boolean isLike) {
        checkReviewInDb(reviewId);
        checkUserInDb(userId);
        reviewLikeDbStorage.deleteLike(reviewId, userId, isLike);
    }

    @Override
    public void addDislike(Long reviewId, Long userId, Boolean isLike) {
        checkReviewInDb(reviewId);
        checkUserInDb(userId);
        reviewLikeDbStorage.addDislike(reviewId, userId, isLike);
    }

    @Override
    public void deleteDislike(Long reviewId, Long userId, Boolean isLike) {
        checkReviewInDb(reviewId);
        checkUserInDb(userId);
        reviewLikeDbStorage.deleteDislike(reviewId, userId, isLike);
    }

    private void checkReviewInDb(Long reviewId) {
        Optional<Review> reviewInDb = Optional.ofNullable(reviewDbStorage.getReviewById(reviewId));
        if (reviewInDb.isEmpty()) {
            throw new ArgumentNotFoundException("Отзыв с id=" + reviewId + " не найден или id=null.");
        }
    }

    private void checkFilmInDb(Long filmId) {
        Optional<Film> filmInDb = Optional.ofNullable(filmDbStorage.getFilmById(filmId));
        if (filmInDb.isEmpty()) {
            throw new ArgumentNotFoundException("Фильм с id=" + filmId + " не найден или id=null.");
        }
    }

    private void checkUserInDb(Long userId) {
        Optional<User> userInDb = Optional.ofNullable(userDbStorage.getUserById(userId));
        if (userInDb.isEmpty()) {
            throw new ArgumentNotFoundException("Юзер с id=" + userInDb + " не найден или id=null.");
        }
    }

}
