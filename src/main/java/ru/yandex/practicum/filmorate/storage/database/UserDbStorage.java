package ru.yandex.practicum.filmorate.storage.database;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.database.rowmappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Component
@Primary
@RequiredArgsConstructor
@Slf4j
@Getter
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FriendDbStorage friendDbStorage;

    // Создать нового пользователя
    @Override
    public User createUser(User user) {
        try {
            String sqlInsertUser =
                    "INSERT INTO users (email, login, name, birthday) " +
                    "VALUES (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement preparedStatement = con.prepareStatement(sqlInsertUser, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, user.getEmail());
                preparedStatement.setString(2, user.getLogin());
                preparedStatement.setString(3, user.getName());
                preparedStatement.setObject(4, user.getBirthday());
                return preparedStatement;
            }, keyHolder);
            Long userId = Objects.requireNonNull(keyHolder.getKey()).longValue();
            user.setId(userId);
            log.info("Добавлен новый пользователь с ID=" + userId + ".");
        } catch (DuplicateKeyException e) {
            throw new ValidationException("Пользователь с логином " + user.getLogin() + " и E-mail " + user.getEmail() + " уже есть в базе.");
        }
        return user;
    }

    // Обновить данные пользователя
    @Override
    public User updateUser(User user) {
        String sqlUpdateUser =
                "UPDATE users " +
                "SET email = ?2, login = ?3, name = ?4, birthday = ?5 " +
                "WHERE user_id = ?1";
        jdbcTemplate.update(sqlUpdateUser, user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        log.info("Внесены изменения в данные пользователя с ID=" + user.getId() + ".");
        return user;
    }

    // Удалить пользователя
    @Override
    public void deleteUser(Long userId) {
        String sqlDeleteUser =
                "DELETE FROM users " +
                "WHERE user_id = ?";
        jdbcTemplate.update(sqlDeleteUser, userId);
        String sqlDeleteFriends =
                "DELETE FROM friend_list " +
                "WHERE user_id = ? " +
                "AND friend_id = ?";
        jdbcTemplate.update(sqlDeleteFriends, userId, userId);
        String sqlDeleteLikes =
                "DELETE FROM like_list " +
                "WHERE user_id = ?";
        jdbcTemplate.update(sqlDeleteLikes, userId);
        log.info("Пользователь с ID=" + userId + " удален из базы.");
    }

    // Получить данные пользователя по ID
    @Override
    public User getUserById(Long userId) {
        String sqlSelectUser =
                "SELECT * " +
                "FROM users " +
                "WHERE user_id = ?";
        List<User> userList = jdbcTemplate.query(sqlSelectUser, new UserRowMapper(), userId);
        return userList.stream()
                .findFirst()
                .orElseThrow(() -> new ArgumentNotFoundException("Пользователь с ID=" + userId + " отсутствует в базе"));
    }

    // Получить список всех пользователей
    @Override
    public List<User> getAllUsers() {
        String sqlSelectAll =
                "SELECT * " +
                "FROM users";
        return jdbcTemplate.query(sqlSelectAll, new UserRowMapper());
    }
}