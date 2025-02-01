package ru.yandex.practicum.filmorate.model.Event;

import lombok.Data;

@Data
public class Event {
    private Long timestamp;
    private Integer userId;
    private EventType eventType;
    private EventOperation operation;
    private Integer eventId;
    private Integer entityId;
}
