package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    // Получить MPA-рейтинг фильма по ID
    @Override
    public Mpa getMpaById(Long id) {
        String sqlSelectMpa =
                "SELECT * " +
                "FROM mpa " +
                "WHERE mpa_id = ?";
        List<Mpa> mpaList = jdbcTemplate.query(sqlSelectMpa,
                (rs, rowNum) -> new Mpa(rs.getLong("mpa_id"), rs.getString("mpa_name")), id);
        return mpaList.stream()
                .findFirst()
                .orElseThrow(() -> new ArgumentNotFoundException("MPA-рейтинг с ID=" + id + " отсутствует в базе"));
    }

    // Получить список всех доступных MPA-рейтингов фильмов
    @Override
    public List<Mpa> getAllMpa() {
        String sqlSelectAll =
                "SELECT * " +
                "FROM mpa " +
                "ORDER BY mpa_id ASC";
        return jdbcTemplate.query(sqlSelectAll,
                (rs, rowNum) -> new Mpa(rs.getLong("mpa_id"), rs.getString("mpa_name")));
    }
}