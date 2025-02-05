package ru.yandex.practicum.filmorate.storage.Events;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event.Event;
import ru.yandex.practicum.filmorate.model.Event.EventOperation;
import ru.yandex.practicum.filmorate.model.Event.EventType;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.EventsRowMapper;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventDbStorageImplementation extends BaseStorage<Event> implements EventDbStorage {
    private final EventsRowMapper eventsRowMapper;

    public EventDbStorageImplementation(JdbcTemplate jdbc, EventsRowMapper eventsRowMapper) {
        super(jdbc);
        this.eventsRowMapper = eventsRowMapper;
    }

    @Override
    public void add(EventType eventType, EventOperation operation, Integer userId, Integer entityId) {
        long timestamp = Instant.now().toEpochMilli();
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbc)
                .withTableName("Events")
                .usingGeneratedKeyColumns("eventId");
        Map<String, Object> param = new HashMap<>();
        param.put("timestamp", timestamp);
        param.put("userId", userId);
        param.put("eventType", eventType.name());
        param.put("operation", operation.name());
        param.put("entityId", entityId);
        Number eventId = simpleJdbcInsert.executeAndReturnKey(param);
        System.out.println("Inserted event with id: " + eventId);
    }

    @Override
    public List<Event> getAll(Integer userId) {
        String getEventsQ = "SELECT eventId, timestamp, userId, eventType, operation, entityId FROM EVENTS WHERE userId = ? ORDER BY timestamp ASC";
        return findMany(eventsRowMapper, getEventsQ, userId);
    }
}
