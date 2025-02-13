package ru.yandex.practicum.filmorate.storage.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event.Event;
import ru.yandex.practicum.filmorate.model.Event.EventOperation;
import ru.yandex.practicum.filmorate.model.Event.EventType;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class EventsRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        Event event = new Event();
        event.setTimestamp(rs.getLong("timestamp"));
        event.setUserId(rs.getInt("userId"));
        event.setEventType(EventType.valueOf(rs.getString("eventType")));
        event.setOperation(EventOperation.valueOf(rs.getString("operation")));
        event.setEventId(rs.getInt("eventId"));
        event.setEntityId(rs.getInt("entityId"));
        return event;
    }
}
