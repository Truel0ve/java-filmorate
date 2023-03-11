package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.interfaces.FriendStorage;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    // Добавить пользователя в друзья
    @Override
    public void addFriend(Long userId, Long friendId) {
        try {
            String sqlSelectStatus =
                    "SELECT status " +
                    "FROM friend_list " +
                    "WHERE user_id = ? " +
                    "AND friend_id = ?";
            Boolean userStatus = jdbcTemplate.queryForObject(sqlSelectStatus, Boolean.class, userId, friendId);
            if (Boolean.TRUE.equals(userStatus)) {
                log.info("Пользователь ID={} уже добавил пользователя ID={} в друзья", userId, friendId);
            } else {
                String sqlUpdateStatus =
                        "UPDATE friend_list " +
                        "SET status = ?3 " +
                        "WHERE user_id = ?1 " +
                        "AND friend_id = ?2";
                jdbcTemplate.update(sqlUpdateStatus, userId, friendId, true);
                log.info("Пользователь ID=" + userId + " одобрил заявку в друзья пользователя ID=" + friendId + "\n" +
                        "Теперь пользователи ID=" + userId + " и ID=" + friendId + " друзья");
            }
        } catch (EmptyResultDataAccessException e) {
            String sqlInsertFriends =
                    "INSERT INTO friend_list (user_id, friend_id, status) " +
                    "VALUES (?, ?, ?)";
            jdbcTemplate.update(sqlInsertFriends,userId, friendId, true);
            jdbcTemplate.update(sqlInsertFriends,friendId, userId, false);
            log.info("Пользователь ID=" + userId + " добавил в друзья пользователя ID=" + friendId + "\n" +
                    "Требуется подтверждение дружбы от пользователя ID=" + friendId);
        }
    }

    // Удалить пользователя из друзей
    @Override
    public void deleteFriend(Long userId, Long friendId) {
        try {
            String sqlDeleteFriend =
                    "DELETE FROM friend_list " +
                    "WHERE user_id = ? " +
                    "AND friend_id = ?";
            jdbcTemplate.update(sqlDeleteFriend, userId, friendId);
            log.info("Пользователь ID={} удалил из друзей пользователя ID={}", userId, friendId);
            String sqlSelectStatus =
                    "SELECT status " +
                    "FROM friend_list " +
                    "WHERE user_id = ? " +
                    "AND friend_id = ?";
            Boolean friendStatus = jdbcTemplate.queryForObject(sqlSelectStatus, Boolean.class, friendId, userId);
            if (Boolean.TRUE.equals(friendStatus)) {
                String sqlUpdateStatus =
                        "UPDATE friend_list " +
                        "SET status = ?3 " +
                        "WHERE user_id = ?1 " +
                        "AND friend_id = ?2";
                jdbcTemplate.update(sqlUpdateStatus,friendId, userId, false);
                log.info("Пользователь ID={} стал подписчиком пользователя ID={}", friendId, userId);
            }
        } catch (EmptyResultDataAccessException e) {
            log.info("Пользователь ID=" + friendId + " отсутствует в списке друзей пользователя ID=" + userId + "\n" +
                    "Удаление невозможно");
        }
    }

    // Получить список всех друзей пользователя по ID
    @Override
    public Set<Long> getAllFriends(Long userId) {
        String sqlSelect =
                "SELECT friend_id " +
                "FROM friend_list " +
                "WHERE user_id = ? " +
                "AND status = true";
        return new HashSet<>(jdbcTemplate.queryForList(sqlSelect, Long.class, userId));
    }
}