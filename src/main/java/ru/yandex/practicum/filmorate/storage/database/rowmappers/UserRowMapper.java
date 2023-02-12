package ru.yandex.practicum.filmorate.storage.database.rowmappers;

import org.springframework.jdbc.core.RowMapper;

import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User(
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate());
        user.setId(rs.getLong("user_id"));
        return user;
    }
}
