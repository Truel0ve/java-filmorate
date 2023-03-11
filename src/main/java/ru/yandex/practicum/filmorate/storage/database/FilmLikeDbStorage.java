package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmLikeStorage;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilmLikeDbStorage implements FilmLikeStorage {
    private final JdbcTemplate jdbcTemplate;

    // Поставить лайк фильму от пользователя
    @Override
    public void addLike(Long filmId, Long userId) {
        String sqlInsertLike =
                "INSERT INTO like_list (film_id, user_id) " +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlInsertLike, filmId, userId);
        log.info("Пользователь ID={} поставил лайк фильму ID={}", userId, filmId);
    }

    // Удалить лайк фильму от пользователя
    @Override
    public void deleteLike(Long filmId, Long userId) {
        String sqlDeleteLike =
                "DELETE FROM like_list " +
                "WHERE film_id = ? " +
                "AND user_id = ?";
        jdbcTemplate.update(sqlDeleteLike, filmId, userId);
        log.info("Пользователь ID={} удалил лайк фильму ID={}", userId, filmId);
    }
}