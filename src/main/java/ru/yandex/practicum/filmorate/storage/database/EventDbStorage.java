package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.database.rowmappers.EventRowMapper;
import ru.yandex.practicum.filmorate.storage.interfaces.EventStorage;

import java.sql.Timestamp;
import java.util.List;

@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class EventDbStorage implements EventStorage {

    private final JdbcTemplate jdbcTemplate;


    // Создать новое событие
    @Override
    public void addEvent(Long userId, Long entityId, String eventType, String operation) {
        String sqlAddEvent = "INSERT INTO events (user_id, entity_id, timestamp, event_type, operation) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlAddEvent, userId, entityId,
                new Timestamp(System.currentTimeMillis()), eventType, operation);
        log.info("Добавлено новое событие пользователя с ID= " + userId + ".");
    }

    // Получить ленту событий юзера
    @Override
    public List<Event> getEvents(Long userId) {
        String sqlGetEvent =
                "SELECT * " +
                "FROM events " +
                "WHERE user_id = ?";
        return jdbcTemplate.query(sqlGetEvent, new EventRowMapper(), userId);
    }
}