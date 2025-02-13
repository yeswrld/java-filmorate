package ru.yandex.practicum.filmorate.storage.Events;

import ru.yandex.practicum.filmorate.model.Event.Event;
import ru.yandex.practicum.filmorate.model.Event.EventOperation;
import ru.yandex.practicum.filmorate.model.Event.EventType;

import java.util.List;

public interface EventDbStorage {
    void add(EventType eventType, EventOperation operation, Integer userId, Integer entityId);

    List<Event> getAll(Integer userId);
}
