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
import ru.yandex.practicum.filmorate.validators.ReviewValidator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewDbStorage reviewDbStorage;
    private final ReviewLikeDbStorage reviewLikeDbStorage;
    private final ReviewValidator reviewValidator;
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    //Поместить отзыв в БД
    public Review createReview(Review review) {
        reviewValidator.validate(review); //Проверка переменных экземпляра класса Review
        Review reviewWithId = reviewDbStorage.createReview(review);

        addEvent(reviewWithId.getUserId(), reviewWithId.getReviewId(), "ADD"); //Добавление события в ленту событий
        return reviewWithId;
    }

    //Обновить отзыв в БД
    public Review updateReview(Review review) {
        reviewValidator.validate(review);
        checkReviewInDb(review.getReviewId()); //Проверка наличия отзыва в БД по его id
        reviewDbStorage.updateReview(review);

        Review reviewWithId = getReviewById(review.getReviewId());
        addEvent(reviewWithId.getUserId(), review.getReviewId(), "UPDATE"); //Добавление события в ленту событий
        return reviewWithId;
    }

    //Удалить отзыв из БД по его id
    public void deleteReview(Long reviewId) {
        checkReviewInDb(reviewId);

        addEvent(getReviewById(reviewId).getUserId(), reviewId, "REMOVE"); //Добавление события в ленту событий
        reviewDbStorage.deleteReview(reviewId);
    }

    //Получить отзыв из БД по его id
    public Review getReviewById(Long reviewId) {
        checkReviewInDb(reviewId);
        Review review = reviewDbStorage.getReviewById(reviewId); //Сначала получаем экземпляр класса
        review.setUseful(getUsefulFromDb(reviewId)); //Затем обновляем переменную экземпляра через сеттер

        return review;
    }

    //Получить все отзывы ко всем фильмам, если указан count - выборку ограничить соответствующим значением
    public List<Review> getAllReviews(Long count) {
        return reviewDbStorage.getAllReviews()
                .stream()
                //Для каждого экземпляра класса Review обновляем поле useful через сеттер
                //Для модификации объектов использую stream.peek()
                .peek(review -> review.setUseful(getUsefulFromDb(review.getReviewId())))
                .sorted(Comparator.comparing(Review::getUseful).reversed()) //Сортируем по убываю по полю useful
                .limit(count)
                .collect(Collectors.toList());
    }

    //Получить все отзывы к фильму по его id, выборку ограничиваем по count
    public List<Review> getAllReviewsForFilm(Long filmId, Long count) {
        return reviewDbStorage.getReviewsForFilm(filmId)
                .stream()
                .peek(review -> review.setUseful(getUsefulFromDb(review.getReviewId())))
                .sorted(Comparator.comparing(Review::getUseful).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    //Добавить лайк к отзыву
    public void addLike(Long reviewId, Long userId, Boolean isLike) {
        checkReviewInDb(reviewId);
        checkUserInDb(userId);
        reviewLikeDbStorage.addLike(reviewId, userId, isLike);
    }

    //Удалить лайк к отзыву
    public void deleteLike(Long reviewId, Long userId, Boolean isLike) {
        checkReviewInDb(reviewId);
        checkUserInDb(userId);
        reviewLikeDbStorage.deleteLike(reviewId, userId, isLike);
    }

    //Добавить дизлайк к отзыву
    public void addDislike(Long reviewId, Long userId, Boolean isLike) {
        checkReviewInDb(reviewId);
        checkUserInDb(userId);
        reviewLikeDbStorage.addDislike(reviewId, userId, isLike);
    }

    //Удалить дизлайк к отзыву
    public void deleteDislike(Long reviewId, Long userId, Boolean isLike) {
        checkReviewInDb(reviewId);
        //checkUserInDb(userId);
        reviewLikeDbStorage.deleteDislike(reviewId, userId, isLike);
    }

    //Проверка id на null, проверка на наличие в БД
    private void checkReviewInDb(Long reviewId) {
        Optional<Review> reviewInDb = Optional.ofNullable(reviewDbStorage.getReviewById(reviewId));
        if (reviewInDb.isEmpty()) {
            throw new ArgumentNotFoundException("Отзыв с id=" + reviewId + " не найден или id=null.");
        }
    }

    //Проверка id на null, проверка на наличие в БД
    private void checkFilmInDb(Long filmId) {
        Optional<Film> filmInDb = Optional.ofNullable(filmDbStorage.getFilmById(filmId));
        if (filmInDb.isEmpty()) {
            throw new ArgumentNotFoundException("Фильм с id=" + filmId + " не найден или id=null.");
        }
    }

    //Проверка id на null, проверка на наличие в БД
    private void checkUserInDb(Long userId) {
        Optional<User> userInDb = Optional.ofNullable(userDbStorage.getUserById(userId));
        if (userInDb.isEmpty()) {
            throw new ArgumentNotFoundException("Юзер с id=" + userInDb + " не найден или id=null.");
        }
    }

    /*Получить рейтинг отзыва (суммируем значения по полю useful в таблице review_like_list).
    Если лайков или дизлайков нет - возвращается null, в таком случае полю useful в экземплярах класса Review
    присваиваем значение 0.*/
    private Long getUsefulFromDb(Long reviewId) {
        Optional<Long> useful = Optional.ofNullable(reviewLikeDbStorage.getUsefulFromDb(reviewId));
        if (useful.isEmpty()) {
            return 0L;
        } else {
            return reviewLikeDbStorage.getUsefulFromDb(reviewId);
        }
    }

    //Добавление отзыва в ленту событий
    public void addEvent(long userId, long reviewId, String operation) {
        userDbStorage.getEventDbStorage().addEvent(userId, reviewId, "REVIEW", operation);
    }

}
