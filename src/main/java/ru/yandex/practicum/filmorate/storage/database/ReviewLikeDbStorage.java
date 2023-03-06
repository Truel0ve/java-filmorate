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

    @Override
    public void addLike(Long reviewId, Long userId, Boolean isLike) {
        //Переменная useful вставляется в поле useful. Если лайк, то +1, если дизлайк, то -1
        //Рейтинг считаем, суммируя значения по полю useful
        Integer useful = 1;
        String sqlQuery = "INSERT INTO REVIEW_LIKE_LIST (REVIEW_ID, USER_ID, IS_LIKE, USEFUL) " +
                "VALUES (?,?,?,?)";
        jdbcTemplate.update(sqlQuery, reviewId, userId, isLike, useful);
        log.info("Поставлен лайк к отзыву с id={} пользователем с id={}", reviewId, userId);
    }

    @Override
    public void deleteLike(Long reviewId, Long userId, Boolean isLike) {
        String sqlQuery = "DELETE FROM REVIEW_LIKE_LIST " +
                "WHERE REVIEW_ID=? AND USER_ID=? AND IS_LIKE=?";
        jdbcTemplate.update(sqlQuery, reviewId, userId, isLike);
        log.info("Удален лайк к отзыву с id={} пользователем с id={}", reviewId, userId);
    }

    @Override
    public void addDislike(Long reviewId, Long userId, Boolean isLike) {
        //Переменная like вставляется в поле useful. Если лайк, то +1, если дизлайк, то -1
        //Рейтинг считаем, суммируя значения по полю useful
        Integer useful = -1;
        String sqlQuery = "INSERT INTO REVIEW_LIKE_LIST (REVIEW_ID, USER_ID, IS_LIKE, USEFUL) " +
                "VALUES (?,?,?,?)";
        jdbcTemplate.update(sqlQuery, reviewId, userId, isLike, useful);
        log.info("Поставлен дизлайк к отзыву с id={} пользователем с id={}", reviewId, userId);
    }

    @Override
    public void deleteDislike(Long reviewId, Long userId, Boolean isLike) {
        String sqlQuery = "DELETE FROM REVIEW_LIKE_LIST " +
                "WHERE REVIEW_ID=? AND USER_ID=? AND IS_LIKE=?";
        jdbcTemplate.update(sqlQuery, reviewId, userId, isLike);
        log.info("Удален дизлайк к отзыву с id={} пользователем с id={}", reviewId, userId);
    }

    public Long getUsefulFromDb(Long reviewId) {
        String sqlQuery = "SELECT SUM(USEFUL) " +
                "FROM REVIEW_LIKE_LIST " +
                "WHERE REVIEW_ID=?";
        Long useful = jdbcTemplate.queryForObject(sqlQuery, Long.class, reviewId);

        log.info("Получен рейтинг полезности к отзыву с id={}", reviewId);
        return useful;
    }

}
