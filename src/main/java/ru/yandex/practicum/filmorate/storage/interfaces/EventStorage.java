package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {

    void addEvent(long userId, long entity_id, String eventType, String operation);

    List<Event> getEvents(long userId);
}

