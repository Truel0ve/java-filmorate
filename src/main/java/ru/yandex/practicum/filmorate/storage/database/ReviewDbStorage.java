package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
@Primary
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review createReview(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlQuery = "INSERT INTO REVIEWS (USER_ID, FILM_ID, CONTENT, IS_POSITIVE) " +
                "VALUES (?,?,?,?) ";
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"REVIEW_ID"});
            stmt.setLong(1, review.getUserId());
            stmt.setLong(2, review.getFilmId());
            stmt.setString(3, review.getContent());
            stmt.setBoolean(4, review.getIsPositive());

            return stmt;
        }, keyHolder);

        Long newReviewId = keyHolder.getKey().longValue();
        review.setReviewId(newReviewId);

        log.info("Добавлен новый отзыв с id={} к фильму с id={} пользователем с id={}",
                review.getReviewId(), review.getFilmId(), review.getUserId());

        return review;
    }

    @Override
    public void updateReview(Review review) {
        //Обновить можно только содержание отзыва (content) и его характер (is_positive)
        String sqlQuery = "UPDATE REVIEWS " +
                "SET CONTENT=?, IS_POSITIVE=? " +
                "WHERE REVIEW_ID=?";
        jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        log.info("Обновлен отзыв с id={} к фильму с id={} от пользователя с id={}",
                review.getReviewId(), review.getFilmId(), review.getUserId());
    }

    @Override
    public void deleteReview(Long reviewId) {
        String sqlQuery = "DELETE FROM REVIEWS " +
                "WHERE REVIEW_ID=?";
        jdbcTemplate.update(sqlQuery, reviewId);

        log.info("Удален отзыв с id={}", reviewId);
    }

    @Override
    public Review getReviewById(Long reviewId) throws ArgumentNotFoundException {
        String sqlQuery = "SELECT * " +
                "FROM REVIEWS " +
                "WHERE REVIEW_ID=?";
        List<Review> reviews = jdbcTemplate.query(sqlQuery, ReviewDbStorage::makeReview, reviewId);
        if (reviews.size() < 1) {
            log.warn("Отзыв с id={} не найден в БД!", reviewId);
            throw new ArgumentNotFoundException("Отзыв с id=" + reviewId + " не найден в БД.");
        }
        log.info("Получен отзыв с id={}", reviewId);

        return reviews.get(0);
    }

    @Override
    public List<Review> getReviewsForFilm(Long filmId) throws ArgumentNotFoundException {
        String sqlQuery = "SELECT * " +
                "FROM REVIEWS " +
                "WHERE FILM_ID=?";

        log.info("Получены все отзывы к фильму с id={}", filmId);
        return jdbcTemplate.query(sqlQuery, ReviewDbStorage::makeReview, filmId);
    }

    @Override
    public List<Review> getAllReviews() throws ArgumentNotFoundException {
        String sqlQuery = "SELECT * " +
                "FROM REVIEWS";

        log.info("Получены все отзывы");
        return jdbcTemplate.query(sqlQuery, ReviewDbStorage::makeReview);
    }

    public static Review makeReview(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("REVIEW_ID");
        Long userId = rs.getLong("USER_ID");
        Long filmId = rs.getLong("FILM_ID");
        String content = rs.getString("CONTENT");
        Boolean isPositive = rs.getBoolean("IS_POSITIVE");
        Long useful = 0L;

        return Review.builder()
                .reviewId(id)
                .userId(userId)
                .filmId(filmId)
                .content(content)
                .isPositive(isPositive)
                .useful(useful)
                .build();
    }

}