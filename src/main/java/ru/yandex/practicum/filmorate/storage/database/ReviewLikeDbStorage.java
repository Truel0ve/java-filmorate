package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewLikeStorage;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReviewLikeDbStorage implements ReviewLikeStorage {
    private final JdbcTemplate jdbcTemplate;

    // Поставить лайк/дизлайк отзыву
    @Override
    public void addLikeOrDislike(Long reviewId, Long userId, String isPositive) {
        String sqlInsert =
                "INSERT INTO review_like_list (review_id, user_id, is_positive) " +
                "VALUES (?,?,?)";
        boolean isLike = Boolean.parseBoolean(isPositive);
        jdbcTemplate.update(sqlInsert, reviewId, userId, isLike);
        updateUseful(reviewId, isLike);
        log.info("Поставлен {} отзыву ID={} пользователем ID={}", getLikeValue(isLike), reviewId, userId);
    }

    // Удалить лайк/дизлайк отзыву
    @Override
    public void deleteLikeOrDislike(Long reviewId, Long userId, String isPositive) {
        String sqlDelete =
                "DELETE FROM review_like_list " +
                "WHERE review_id = ? AND user_id = ? AND is_positive = ?";
        boolean isLike = Boolean.parseBoolean(isPositive);
        jdbcTemplate.update(sqlDelete, reviewId, userId, isLike);
        updateUseful(reviewId, !isLike);
        log.info("Удален {} отзыву ID={} пользователем ID={}", getLikeValue(isLike), reviewId, userId);
    }

    // Обновить рейтинг полезности отзыва
    private void updateUseful(Long reviewId, boolean isLike) {
        String sqlUpdateUseful;
        if (isLike) {
            sqlUpdateUseful =
                    "UPDATE reviews " +
                    "SET useful = useful + 1 " +
                    "WHERE review_id = ?";
        } else {
            sqlUpdateUseful =
                    "UPDATE reviews " +
                    "SET useful = useful - 1 " +
                    "WHERE review_id = ?";
        }
        jdbcTemplate.update(sqlUpdateUseful, reviewId);
    }

    // Определить лайк/дизлайк
    private String getLikeValue(boolean isLike) {
        return (isLike) ? "лайк" : "дизлайк";
    }
}