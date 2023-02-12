package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.database.rowmappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User createUser(User user) {
        try {
            Optional<String> emailOpt = Optional.ofNullable(
                    jdbcTemplate.queryForObject("select email from users where email = ?", String.class, user.getEmail()));
            if (emailOpt.isPresent()) {
                throw new ValidationException("Пользователь с E-mail " + user.getEmail() + " уже есть в базе.");
            } else {
                throw new EmptyResultDataAccessException(0);
            }
        } catch (EmptyResultDataAccessException e) {
            String sqlInsertUser = "insert into users (email, login, name, birthday) values (?, ?, ?, ?)";
            jdbcTemplate.update(sqlInsertUser, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
            String sqlSelectId = "select user_id from users where email = ?";
            Optional<Long> userIdOpt = Optional.ofNullable(jdbcTemplate.queryForObject(sqlSelectId, Long.class, user.getEmail()));
            if (userIdOpt.isPresent()) {
                long userId = userIdOpt.get();
                user.setId(userId);
                log.info("Добавлен новый пользователь с ID=" + userId + ".");
            } else {
                throw new NullPointerException("При добавлении пользователя произошла ошибка на стороне сервера.");
            }
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        jdbcTemplate.update("update users set email = ?2, login = ?3, name = ?4, birthday = ?5 where user_id = ?1",
                user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        log.info("Внесены изменения в данные пользователя с ID=" + user.getId() + ".");
        return user;
    }

    @Override
    public void deleteUser(User user) {
        jdbcTemplate.update("delete from users where user_id = ?", user.getId());
        log.info("Пользователь с ID=" + user.getId() + " удален из базы.");
    }

    public User getUserById(Long userId) throws NullPointerException {
        List<User> userList = jdbcTemplate.query("select * from users where user_id = ?", new UserRowMapper(), userId);
        return userList.stream()
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Пользователь с ID=" + userId + " отсутствует в базе"));
    }

    public Map<Long, User> getAllUsers() {
        Stream<User> userStream = jdbcTemplate.queryForStream("select * from users", new UserRowMapper());
        return userStream.collect(Collectors.toMap(User::getId, user -> user));
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        try {
            Optional<Boolean> statusOpt = Optional.ofNullable(
                    jdbcTemplate.queryForObject("select status from friend_list where user_id = ? and friend_id = ?", Boolean.class, userId, friendId));
            log.info("Добавляем в друзья.");
            if (statusOpt.isPresent()) {
                if (Boolean.TRUE.equals(statusOpt.get())) {
                    log.info("Пользователь с ID=" + userId + " уже добавил пользователя с ID=" + friendId + " в друзья.");
                } else {
                    jdbcTemplate.update("update friend_list set status = ?3 where user_id = ?1 and friend_id = ?2", userId, friendId, true);
                    log.info("Пользователь с ID=" + userId + " одобрил заявку в друзья пользователя с ID=" + friendId + "." +
                            " Теперь пользователи с ID=" + userId + " и ID=" + friendId + " друзья.");
                }
            }
        } catch (EmptyResultDataAccessException e) {
            String sqlInsert = "insert into friend_list (user_id, friend_id, status) values (?, ?, ?)";
            jdbcTemplate.update(sqlInsert,userId, friendId, true);
            jdbcTemplate.update(sqlInsert,friendId, userId, false);
            log.info("Пользователь с ID=" + userId + " добавил в друзья пользователя с ID=" + friendId + "." +
                    " Требуется подтверждение дружбы от пользователя с ID=" + friendId + ".");
        }
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        String sqlSelect = "select * from friend_list where user_id = ? and friend_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlSelect, userId, friendId);
        SqlRowSet friendRows = jdbcTemplate.queryForRowSet(sqlSelect, friendId, userId);
        if (!userRows.next()) {
            log.info("Пользователь с ID=" + friendId + " отсутствует в списке друзей пользователя с ID=" + userId + "." +
                    " Удаление невозможно.");
        } else {
            getUserById(userId).getFriends().remove(friendId);
            jdbcTemplate.update("delete from friend_list where user_id = ? and friend_id = ?", userId, friendId);
            log.info("Пользователь с ID=" + userId + " удалил из друзей пользователя с ID=" + userId + ".");
            if (friendRows.next()) {
                jdbcTemplate.update("update friend_list set status = ?3 where user_id = ?1 and friend_id = ?2",
                        friendId, userId, false);
                log.info("Пользователь с ID=" + friendId + " стал подписчиком пользователя с ID=" + userId + ".");
            }
        }
    }

    public Set<Long> getFriends(Long userId) {
        String sqlSelect = "select friend_id from friend_list where user_id = ? and status = true group by user_id";
        return new HashSet<>(jdbcTemplate.queryForList(sqlSelect, Long.class, userId));
    }
}