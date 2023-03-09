package ru.yandex.practicum.filmorate.model;


import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class Event {

    long eventId;

    long userId;

    String eventType;

    String operation;

    long entityId;

    long timestamp;


}
