package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.database.rowmappers.EventRowMapper;
import ru.yandex.practicum.filmorate.storage.interfaces.EventStorage;

import java.sql.Timestamp;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    // Создать новое событие
    @Override
    public void addEvent(Long userId, Long entityId, String eventType, String operation) {
        String sqlInsertEvent =
                "INSERT INTO events (user_id, entity_id, timestamp, event_type, operation) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlInsertEvent, userId, entityId, new Timestamp(System.currentTimeMillis()), eventType, operation);
        log.info("Добавлено новое событие пользователя ID={}", userId);
    }

    // Получить ленту событий пользователя
    @Override
    public List<Event> getAllEvents(Long userId) {
        String sqlSelectEvent =
                "SELECT * " +
                "FROM events " +
                "WHERE user_id = ?";
        return jdbcTemplate.query(sqlSelectEvent, new EventRowMapper(), userId);
    }
}