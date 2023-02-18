package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    // Получить MPA-рейтинг фильма по ID
    public Mpa getMpaById(int id) {
        String sqlSelectMpa =
                "SELECT * " +
                "FROM mpa " +
                "WHERE mpa_id = ?";
        List<Mpa> mpaList = jdbcTemplate.query(sqlSelectMpa,
                (rs, rowNum) -> new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")), id);
        return mpaList.stream()
                .findFirst()
                .orElseThrow(() -> new NullPointerException("MPA-рейтинг с ID=" + id + " отсутствует в базе"));
    }

    // Получить список всех доступных MPA-рейтингов фильмов
    public List<Mpa> getAllMpa() {
        String sqlSelectAll =
                "SELECT * " +
                "FROM mpa " +
                "ORDER BY mpa_id ASC";
        return jdbcTemplate.query(sqlSelectAll,
                (rs, rowNum) -> new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
    }
}