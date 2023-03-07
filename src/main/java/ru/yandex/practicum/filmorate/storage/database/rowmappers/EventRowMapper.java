package ru.yandex.practicum.filmorate.storage.database.rowmappers;

import ru.yandex.practicum.filmorate.model.Event;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EventRowMapper implements RowMapper<Event> {

    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getLong("event_id"))
                .userId(rs.getLong("user_id"))
                .entityId(rs.getLong("entity_id"))
                .timestamp(rs.getTimestamp("timestamp").getTime())
                .eventType(rs.getString("event_type"))
                .operation(rs.getString("operation"))
                .build();
    }
}
