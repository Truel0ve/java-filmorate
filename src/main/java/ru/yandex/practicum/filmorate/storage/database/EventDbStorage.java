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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class EventDbStorage implements EventStorage {

    private final JdbcTemplate jdbcTemplate;


    // Создать новое событие
    @Override
    public void addEvent(long userId, long entity_id, String eventType, String operation) {
        String sqlAddEvent = "INSERT INTO events (user_id, entity_id, timestamp, event_type, operation) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlAddEvent, userId, entity_id,
                new Timestamp(System.currentTimeMillis()), eventType, operation);
        log.info("Добавлено новое событие пользрвателя с ID= " + userId + ".");
    }

    // Получить ленту событий юзера
    @Override
    public List<Event> getEvents(long userId) {
        String sqlSelectFriends =
                "SELECT friend_id " +
                        "FROM friend_list " +
                        "WHERE user_id = ? " +
                        "AND status = true";
        HashSet<Long> friends = new HashSet<>(jdbcTemplate.queryForList(sqlSelectFriends, Long.class, userId));

        List<Event> events = new ArrayList<>();

       // for (Long friendId : friends) {
            String sqlGetEvent = "SELECT * FROM events WHERE user_id = ?";
            events.addAll(jdbcTemplate.queryForStream(
                    sqlGetEvent, new EventRowMapper(), userId).collect(Collectors.toList()));
      //  }
        return events;
    }
}
