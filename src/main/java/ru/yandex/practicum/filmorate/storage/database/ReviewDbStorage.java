package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.database.rowmappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    // Создать новый отзыв
    @Override
    public Review createReview(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlInsertReview =
                "INSERT INTO reviews (user_id, film_id, content, is_positive) " +
                "VALUES (?,?,?,?) ";
        jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(sqlInsertReview, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, review.getUserId());
            preparedStatement.setLong(2, review.getFilmId());
            preparedStatement.setString(3, review.getContent());
            preparedStatement.setBoolean(4, review.getIsPositive());
            return preparedStatement;
        }, keyHolder);
        Long reviewId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        review.setReviewId(reviewId);
        log.info("Добавлен новый отзыв ID={} к фильму ID={} пользователем ID={}",
                review.getReviewId(), review.getFilmId(), review.getUserId());
        return review;
    }

    // Обновить контент и/или статус отзыва
    @Override
    public Review updateReview(Review review) {
        String sqlUpdateReview =
                "UPDATE reviews " +
                "SET content = ?, is_positive = ? " +
                "WHERE review_id = ?";
        jdbcTemplate.update(sqlUpdateReview, review.getContent(), review.getIsPositive(), review.getReviewId());
        log.info("Обновлен отзыв ID={} к фильму ID={} от пользователя ID={}",
                review.getReviewId(), review.getFilmId(), review.getUserId());
        return getReviewById(review.getReviewId());
    }

    // Удалить отзыв
    @Override
    public void deleteReview(Long reviewId) {
        String sqlDeleteReview =
                "DELETE FROM reviews " +
                "WHERE review_id = ?";
        jdbcTemplate.update(sqlDeleteReview, reviewId);
        String sqlDeleteLikes =
                "DELETE FROM review_like_list " +
                "WHERE review_id = ?";
        jdbcTemplate.update(sqlDeleteLikes, reviewId);
        log.info("Удален отзыв ID={}", reviewId);
    }

    // Получить отзыв по ID
    @Override
    public Review getReviewById(Long reviewId) throws ArgumentNotFoundException {
        String sqlQuery =
                "SELECT * " +
                "FROM reviews " +
                "WHERE review_id = ?";
        List<Review> reviewList = jdbcTemplate.query(sqlQuery, new ReviewRowMapper(), reviewId);
        return reviewList.stream()
                .findFirst()
                .orElseThrow(() -> new ArgumentNotFoundException("Отзыв с ID=" + reviewId + " отсутствует в базе"));
    }

    // Получить заданное количество отзывов к фильму с заданным ID
    @Override
    public List<Review> getAllReviewsForFilm(Long filmId, Long count) {
        String sqlSelectAll =
                "SELECT * " +
                "FROM reviews " +
                "WHERE film_id = ? " +
                "ORDER BY review_id ASC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlSelectAll, new ReviewRowMapper(), filmId, count);
    }

    // Получить заданное количество отзывов из всех имеющихся
    @Override
    public List<Review> getAllReviews(Long count) {
        String sqlSelectAll =
                "SELECT * " +
                "FROM reviews " +
                "ORDER BY review_id ASC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlSelectAll, new ReviewRowMapper(), count);
    }
}