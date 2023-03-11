package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.database.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.EventStorage;
import ru.yandex.practicum.filmorate.validators.IdValidator;

import java.util.List;

@Service
@AllArgsConstructor
public class EventService implements EventStorage {
    private final EventDbStorage eventStorage;
    private final IdValidator idValidator;

    // Создать событие пользователя
    @Override
    public void addEvent(Long userId, Long entityId, String eventType, String operation) {
        idValidator.validateUserId(userId);
        eventStorage.addEvent(userId, entityId, eventType, operation);
    }

    // Получить все события пользователя
    @Override
    public List<Event> getAllEvents(Long userId) {
        idValidator.validateUserId(userId);
        return eventStorage.getAllEvents(userId);
    }
}